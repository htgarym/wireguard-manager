package com.hightouchinc.command.render

import java.util.concurrent.Callable
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "render",
   description = ["Manage configuration"],
   mixinStandardHelpOptions = true,
   subcommands = [
      RenderServerConfigurationCommand::class,
      RenderPeerConfigurationCommand::class,
  ],
)
class RenderCommand: Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(RenderCommand::class.java)

   @CommandLine.Spec
   lateinit var spec: CommandLine.Model.CommandSpec

   private var exitCode = 0

   override fun call(): Int {
      logger.warn("No Render subcommand offered.")

      spec.commandLine().usage(System.out)

      exitCode = 1

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
