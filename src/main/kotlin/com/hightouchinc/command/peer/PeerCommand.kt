package com.hightouchinc.command.peer

import java.util.concurrent.Callable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "peer",
   description = ["Manage peers"],
   mixinStandardHelpOptions = true,
   subcommands = [PeerAddCommand::class]
)
class PeerCommand: Callable<Int>, CommandLine.IExitCodeGenerator {

   @CommandLine.Spec
   lateinit var spec: CommandLine.Model.CommandSpec
   private val logger: Logger = LoggerFactory.getLogger(PeerCommand::class.java)

   private var exitCode = 0

   override fun call(): Int {
      logger.error("No Add or Delete subcommand offered.")

      spec.commandLine().usage(System.out)

      exitCode = 1

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
