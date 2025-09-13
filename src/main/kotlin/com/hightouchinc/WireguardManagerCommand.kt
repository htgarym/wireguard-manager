package com.hightouchinc

import com.hightouchinc.command.init.InitCommand
import io.micronaut.configuration.picocli.PicocliRunner
import java.util.concurrent.Callable
import kotlin.system.exitProcess
import picocli.CommandLine

@CommandLine.Command(
   version = ["1.0"],
   name = "wireguard-manager",
   description = ["CLI tool to manage a wireguard server and generate client configurations"],
   mixinStandardHelpOptions = true,
   subcommands = [
      InitCommand::class,
   ],
)
class WireguardManagerCommand : Callable<Int> {

   @CommandLine.Spec
   lateinit var spec: CommandLine.Model.CommandSpec

   override fun call(): Int {
      spec.commandLine().usage(System.out)

      return 1
   }

   companion object {
      @JvmStatic fun main(args: Array<String>) {
         val execResult: Int? = PicocliRunner.call(WireguardManagerCommand::class.java, *args)

         exitProcess(execResult ?: 0)
      }
   }
}
