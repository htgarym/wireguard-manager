package com.hightouchinc.command.init

import com.hightouchinc.model.CompletionError
import com.hightouchinc.model.PrivateWireGuardKey
import com.hightouchinc.model.PublicWireGuardKey
import com.hightouchinc.model.Server
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.IpV4Address
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Text
import com.hightouchinc.persistence.server.ServerEntity
import com.hightouchinc.persistence.server.ServerRepository
import com.hightouchinc.templating.TemplatingService
import jakarta.inject.Inject
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.nio.charset.Charset
import java.util.concurrent.Callable
import org.apache.commons.io.output.ByteArrayOutputStream
import org.jline.consoleui.prompt.ConsolePrompt
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import picocli.CommandLine

@CommandLine.Command(
   name = "initialize",
   aliases = ["init"],
   description = ["Initialize server"],
)
class InitCommand @Inject constructor(
   private val serverRepository: ServerRepository,
   private val templatingService: TemplatingService,
) : Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(InitCommand::class.java)
   private var exitCode = 0

   private fun generateWireGuardServerKeys(): Outcome<Pair<PrivateWireGuardKey, PublicWireGuardKey>, CompletionError> {
      val keyOutput = ByteArrayOutputStream()

      ProcessExecutor()
         .command("wg", "genkey")
         .redirectOutput(keyOutput)
         .execute()

      val privateKeyGenerated = keyOutput.toString(Charset.defaultCharset()).trim()
      val privateKeyInputStream = ByteArrayInputStream(keyOutput.toByteArray())

      keyOutput.reset()
      ProcessExecutor()
         .command("wg", "pubkey")
         .redirectOutput(keyOutput)
         .redirectInput(privateKeyInputStream)
         .execute()

      val publicKeyGenerated = keyOutput.toString(Charset.defaultCharset()).trim()

      logger.debug("Generated private key: {}", privateKeyGenerated)
      logger.debug("Generated public key: {}", publicKeyGenerated)

      return when(val privateKey = PrivateWireGuardKey.create(privateKeyGenerated)) {
         is Outcome.Failure -> Outcome.Failure(CompletionError.InvalidWireGuardPrivateKey(privateKeyGenerated))
         is Outcome.Success -> {
            when(val publicKey = PublicWireGuardKey.create(publicKeyGenerated)) {
               is Outcome.Failure -> Outcome.Failure(CompletionError.InvalidWireGuardPublicKey(publicKeyGenerated))
               is Outcome.Success -> {
                  Outcome.Success(privateKey.value to publicKey.value)
               }
            }
         }
      }
   }

   override fun call(): Int {
      logger.info("Initializing application")

      if(serverRepository.count() < 1) {
         val promptResult = TerminalBuilder.builder().system(true).build().use { terminal ->
            val prompt = ConsolePrompt(terminal)

            prompt.prompt(
               prompt.promptBuilder
                  .buildPromptForInterface("interface")
                  .buildPublicIpAddress("publicAddress")
                  .build(),
            )
         }

         exitCode = if (promptResult.size != 2) {
            logger.error("Not all values were captured")

            1
         } else if (!promptResult.containsKey("interface")) {
            logger.error("Interface was not captured")

            2
         } else {
            val ifacePrompt = promptResult["interface"]!!.result
            val iface = Text.create(ifacePrompt)
            val publicAddressPrompt = promptResult["publicAddress"]!!.result

            when (val publicAddress = IpV4Address.create(publicAddressPrompt)) {
               is Outcome.Failure -> {
                  CompletionError.printError(logger, publicAddress)

                  3
               }

               is Outcome.Success -> {
                  when (val keys = generateWireGuardServerKeys()) {
                     is Outcome.Failure -> {
                        CompletionError.printError(logger, keys)

                        4
                     }

                     is Outcome.Success -> {
                        val (privateKey, publicKey) = keys.value
                        val server = Server(
                           id = Identifier.create(),
                           iface = iface,
                           publicKey = publicKey,
                           privateKey = privateKey,
                           publicAddress = publicAddress.value
                        )

                        val writer = StringWriter()

                        templatingService.renderServerConfig(server, emptyList(), writer)

                        serverRepository.save(ServerEntity(server))

                        0
                     }
                  }
               }
            }
         }
      } else {
         logger.info("Server has been configured nothing to do.")
      }

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
