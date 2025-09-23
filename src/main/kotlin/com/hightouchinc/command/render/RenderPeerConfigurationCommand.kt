package com.hightouchinc.command.render

import com.hightouchinc.model.Server
import com.hightouchinc.persistence.peer.PeerEntity
import com.hightouchinc.persistence.peer.PeerRepository
import com.hightouchinc.persistence.server.ServerEntity
import com.hightouchinc.persistence.server.ServerRepository
import jakarta.inject.Inject
import java.util.concurrent.Callable
import org.jline.consoleui.prompt.ConsolePrompt
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "peer",
   description = ["Render peer configuration"],
   mixinStandardHelpOptions = true,
)
class RenderPeerConfigurationCommand @Inject constructor(
   private val peerRepository: PeerRepository,
   private val serverRepository: ServerRepository,
) : Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(RenderPeerConfigurationCommand::class.java)
   private var exitCode = 0

   override fun call(): Int {
      val servers = serverRepository.findAll().map(ServerEntity::toModel)
      val server = if (servers.isEmpty()) {
         logger.info("No servers found")

         exitCode = 1

         return exitCode
      } else if (servers.size == 1) {
         servers.first()
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

            exitCode = 2
            null
         } else {
            val serverId = promptResult["server"]!!.result

            servers.find { it.id.value.toString() == serverId }
         }
      }

      exitCode = if (server != null) {
         val peers = peerRepository.findByServerId(server.id.value).map(PeerEntity::toModel)

         if (peers.isNotEmpty()) {
            val promptResult = TerminalBuilder.builder().system(true).build().use { terminal ->
               val prompt = ConsolePrompt(terminal)

               prompt.prompt(
                  prompt.promptBuilder
                     .buildPeersPrompt("peer", peers)
                     .build(),
               )
            }

            if (promptResult.size != 1) {
               logger.info("No peers chosen")

               3
            } else {
               val peerId = promptResult["peer"]!!.result
               val peer = peers.find { it.id.value.toString() == peerId }

               if (peer == null) {
                  logger.error("Peer {} not found", peerId)

                  6
               } else {
                  println(peer.clientConfig.value)

                  0
               }
            }
         } else {
            logger.error("No peers found for server {}", server.iface.value)

            4
         }
      } else {
         logger.error("Server not found")

         5
      }

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
