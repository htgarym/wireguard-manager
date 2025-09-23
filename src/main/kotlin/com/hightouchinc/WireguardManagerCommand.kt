package com.hightouchinc

import com.hightouchinc.command.database.Database
import com.hightouchinc.command.init.InitCommand
import com.hightouchinc.command.peer.PeerCommand
import com.hightouchinc.command.render.RenderCommand
import io.micronaut.configuration.picocli.PicocliRunner
import java.util.concurrent.Callable
import kotlin.system.exitProcess
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   version = ["0.0.3"],
   name = "wireguard-manager",
   description = ["CLI tool to manage a wireguard server and generate client configurations"],
   mixinStandardHelpOptions = true,
   subcommands = [
      InitCommand::class,
      PeerCommand::class,
      RenderCommand::class,
      Database::class,
   ],
)
class WireguardManagerCommand : Callable<Int>, CommandLine.IExitCodeGenerator {

   @CommandLine.Spec
   lateinit var spec: CommandLine.Model.CommandSpec

   private var exitCode = 0

   override fun call(): Int {
      spec.commandLine().usage(System.out)

      exitCode = 1

      return exitCode
   }

   override fun getExitCode(): Int = exitCode

   companion object {

      @JvmStatic
      fun main(args: Array<String>) {
         val logger = LoggerFactory.getLogger(WireguardManagerCommand::class.java)

         val exitCode = try {
            val result: Int? = PicocliRunner.call(WireguardManagerCommand::class.java, *args)

            result ?: 0 // FIXME: status is not being returned from commands, need to figure out why
         } catch (e: Throwable) {
            logger.error("Unexpected error while running Wireguard manager", e)

            1
         }

         exitProcess(exitCode)
      }
   }
}
