package com.hightouchinc.command.user

import java.util.concurrent.Callable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "user",
   description = ["Manage users"],
   mixinStandardHelpOptions = true,
   subcommands = [UserAddCommand::class]
)
class UserCommand: Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger: Logger = LoggerFactory.getLogger(UserCommand::class.java)

   private var exitCode = 0

   override fun call(): Int {
      logger.error("No Add or Delete subcommand offered.")

      exitCode = 1

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
