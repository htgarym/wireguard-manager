# Repository Guidelines

## Project Structure & Module Organization
Source resides in `src/main/kotlin/com/hightouchinc`, with command-line entrypoints in `WireguardManagerCommand.kt` and supporting models and templating services nearby. Configuration files live in `src/main/resources`, including Flyway migrations under `db` and JTE templates under `template`. Tests are in `src/test/groovy`, organised as Spock specifications mirroring the main package layout. Gradle build logic is defined in `build.gradle` and `gradle.properties`.

## Build, Test, and Development Commands
- `./gradlew build` compiles the CLI, runs unit tests, and produces the staged artifacts in `build/`.
- `./gradlew test` runs the Spock specification suite only; use when iterating.
- `./gradlew shadowJar` assembles a runnable fat jar at `build/libs/wireguard-manager-all.jar` for manual installs.
- `./gradlew run --args="--help"` executes the CLI locally without assembling a jar, useful for smoke-checking argument wiring.

## Runtime & Configuration Tips
Defaults in `application.properties` target production paths: the H2 database and rendered configs go under `/etc/wireguard`. For local development, export `WIREGUARD_ROOT=$(pwd)/build/dev-wireguard` (or similar) before running commands so state stays in the workspace. Verify that directory exists or Gradle tasks will create it on demand.

## Coding Style & Naming Conventions
Kotlin code uses the Kotlin-official profile with three-space indentation, a 120-character line limit, and trailing commas enabled in multiline literals (see `.editorconfig`). Avoid star imports—leave IntelliJ on the repo defaults—and prefer expression bodies only when they stay within the line limit. Keep DTOs as data classes, keep service classes focused, and name JTE templates with snake_case filenames that mirror the rendered resource. CLI command names stay kebab-case to match Picocli options.

## Testing Guidelines
Spock specs live alongside the code they verify; name them `*Spec.groovy` and mirror the package path. Each new command branch or templating rule should have at least one specification covering success and validation failures. Run `./gradlew test` prior to opening a PR; CI expects a clean `build` run. If adding integration-level behaviour, stub external processes via ZT-Exec to avoid system calls.

## Commit & Pull Request Guidelines
Commit messages follow short, imperative summaries (`Add peer templating fallback`), optionally followed by detail in the body. Group related Kotlin and resource changes together so reviewers can trace template updates. Pull requests should link relevant issues, describe runtime impacts, and include before/after snippets for rendered configs when templates change. Confirm the chosen `WIREGUARD_ROOT` path used during testing in the PR description to help reviewers reproduce results.

## Do
- use Micronaut version specified in `gradle.properties`
- prefer library solutions over writing custom code
- use Micronaut to the fullest

## Safety and permissions
Allowed without prompt:
- read files, list files
- ./gradlew test
- ./gradlew tasks

Ask first:
- git commit
- git push
- deleting files
