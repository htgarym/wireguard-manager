package com.hightouchinc.command.init

import com.hightouchinc.model.Server
import com.hightouchinc.model.base.FirstThreeOctets
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Port
import com.hightouchinc.model.base.Text
import com.hightouchinc.persistence.server.ServerEntity
import com.hightouchinc.persistence.server.ServerRepository
import jakarta.inject.Inject
import java.util.concurrent.Callable
import org.jline.consoleui.prompt.ConsolePrompt
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "init",
   aliases = ["initialize"],
   description = ["Initialize application"],
)
class InitCommand @Inject constructor(
   private val serverRepository: ServerRepository
) : Callable<Int> {
   private val logger = LoggerFactory.getLogger(InitCommand::class.java)

   override fun call(): Int {
      logger.info("Initializing application")

      val promptResult = TerminalBuilder.builder().system(true).build().use { terminal ->
         val prompt = ConsolePrompt(terminal)

         prompt.prompt(
            prompt.promptBuilder
               .buildPromptForInterface("interface")
               .buildFirstThreeOctet("firstThree")
               .buildPromptForPort("port")
               .build(),
         )
      }

      return if (promptResult.size != 3) {
         logger.error("Not all values were captured")

         -1
      } else if (!promptResult.containsKey("interface")) {
         logger.error("Interface was not captured")

         -2
      } else if (!promptResult.containsKey("firstThree")) {
         logger.error("First Three Octets was not captured")

         -3
      } else if (!promptResult.containsKey("port")) {
         logger.error("Port was not captured")
         -4
      } else {
         val ifacePrompt = promptResult["interface"]!!.result
         val firstThreeOctetsPrompt = promptResult["firstThree"]!!.result
         val portPrompt = promptResult["port"]!!.result
         val iface = Text.create(ifacePrompt)
         val firstThreeOctets = FirstThreeOctets.create(firstThreeOctetsPrompt)
         val port = Port.create(portPrompt)

         when {
            firstThreeOctets is Outcome.Success<FirstThreeOctets> && port is Outcome.Success<Port> -> {
               serverRepository.save(
                  ServerEntity(
                     Server(
                        id = Identifier(),
                        iface = iface,
                        firstThreeOctets = firstThreeOctets.value,
                        port = port.value,
                     )
                  )
               )
               0
            }

            else -> {
               -5
            }
         }
      }
   }
}
