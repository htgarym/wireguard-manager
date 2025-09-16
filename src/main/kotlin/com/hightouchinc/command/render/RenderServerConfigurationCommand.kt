package com.hightouchinc.command.render

import com.hightouchinc.model.Server
import com.hightouchinc.persistence.peer.PeerEntity
import com.hightouchinc.persistence.peer.PeerRepository
import com.hightouchinc.persistence.server.ServerEntity
import com.hightouchinc.persistence.server.ServerRepository
import com.hightouchinc.templating.TemplatingService
import io.micronaut.context.annotation.Value
import jakarta.inject.Inject
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Callable
import org.jline.consoleui.prompt.ConsolePrompt
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "server",
   description = ["Manage server configuration"],
   mixinStandardHelpOptions = true,
)
class RenderServerConfigurationCommand @Inject constructor(
   private val peerRepository: PeerRepository,
   private val serverRepository: ServerRepository,
   private val templatingService: TemplatingService,
   @param:Value("\${wireguard.manager.root}") private val wireGuardManagerRoot: String,
): Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(RenderServerConfigurationCommand::class.java)
   private var exitCode = 0

   private fun renderServerConfig(server: Server) {
      val peers = peerRepository.findByServerId(server.id.value).map(PeerEntity::toModel)

      FileWriter(File(wireGuardManagerRoot, "wg0.conf")).use { fw ->  // TODO: This needs to be configuration on the server table that is auto-incrementing or something
         templatingService.renderServerConfig(server, peers, fw)

         fw.flush()
      }
   }

   override fun call(): Int {
      val servers = serverRepository.findAll().map(ServerEntity::toModel)


      exitCode = if (servers.isEmpty()) {
         logger.info("No servers found")

         1
      } else if (servers.size == 1) {
         val server = servers.first()

         renderServerConfig(server)

         0
      } else {
         val promptResult = TerminalBuilder.builder().system(true).build().use { terminal ->
            val prompt = ConsolePrompt(terminal)

            prompt.prompt(
               prompt.promptBuilder
                  .buildServerChooserPrompt("server", servers)
                  .build(),
            )
         }

         if (promptResult.size != 1) {
            logger.error("Server not chosen.")

            2
         } else {
            val serverId = promptResult["server"]!!.result
            val server = servers.find { it.id.value.toString() == serverId }

            if (server != null) {
               renderServerConfig(server)

               0
            } else {
               logger.error("Server {} not found", serverId)

               3
            }
         }
      }

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
