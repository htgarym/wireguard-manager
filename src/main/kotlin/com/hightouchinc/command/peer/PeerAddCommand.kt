package com.hightouchinc.command.peer

import com.hightouchinc.command.init.InitCommand
import com.hightouchinc.model.CompletionError
import com.hightouchinc.model.Peer
import com.hightouchinc.model.PresharedWireGuardKey
import com.hightouchinc.model.PrivateWireGuardKey
import com.hightouchinc.model.PublicWireGuardKey
import com.hightouchinc.model.base.FourthOctet
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Text
import com.hightouchinc.persistence.peer.PeerEntity
import com.hightouchinc.persistence.peer.PeerRepository
import com.hightouchinc.persistence.server.ServerRepository
import com.hightouchinc.templating.TemplatingService
import jakarta.inject.Inject
import java.io.ByteArrayInputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.concurrent.Callable
import org.apache.commons.io.output.ByteArrayOutputStream
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import picocli.CommandLine

@CommandLine.Command(
   name = "add",
   description = ["Add a user to the server"],
   mixinStandardHelpOptions = true,
)
class PeerAddCommand @Inject constructor(
   private val initCommand: InitCommand,
   private val peerRepository: PeerRepository,
   private val serverRepository: ServerRepository,
   private val templatingService: TemplatingService,
) : Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(PeerAddCommand::class.java)
   private var exitCode = 0

   @CommandLine.Parameters(arity = "0", paramLabel = "USER", description = ["Username"])
   lateinit var username: String

   @CommandLine.Parameters(arity = "1", paramLabel = "FOURTH_OCTET", description = ["Fourth Octet"], defaultValue = "2")
   var fourthOctetIn: Int = 0

   private fun generateClientKeys(): Outcome<Triple<PrivateWireGuardKey, PublicWireGuardKey, PresharedWireGuardKey>, CompletionError> {
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

      keyOutput.reset()
      ProcessExecutor()
         .command("wg", "genpsk")
         .redirectOutput(keyOutput)
         .execute()

      val presharedKeyGenerated = keyOutput.toString(Charset.defaultCharset()).trim()

      logger.debug("Generated private key: {}", privateKeyGenerated)
      logger.debug("Generated public key: {}", publicKeyGenerated)
      logger.debug("Generated preshared key: {}", presharedKeyGenerated)

      return when(val privateKey = PrivateWireGuardKey.create(privateKeyGenerated)) {
         is Outcome.Failure -> Outcome.Failure(CompletionError.InvalidWireGuardPrivateKey(privateKeyGenerated))
         is Outcome.Success -> {
            when(val publicKey = PublicWireGuardKey.create(publicKeyGenerated)) {
               is Outcome.Failure -> Outcome.Failure(CompletionError.InvalidWireGuardPublicKey(publicKeyGenerated))
               is Outcome.Success -> {
                  when(val presharedKey = PresharedWireGuardKey.create(presharedKeyGenerated)) {
                     is Outcome.Failure -> Outcome.Failure(CompletionError.InvalidWireGuardPresharedKey(presharedKeyGenerated))
                     is Outcome.Success -> {
                        Outcome.Success(Triple(privateKey.value, publicKey.value, presharedKey.value))
                     }
                  }
               }
            }
         }
      }
   }

   override fun call(): Int {
      if (serverRepository.count() < 1) {
         exitCode = initCommand.call()
      }

      if (exitCode == 0) {
         exitCode = when (val fourthOctet = FourthOctet.create(fourthOctetIn)) {
            is Outcome.Failure -> {
               CompletionError.printError(logger, fourthOctet)

               1
            }

            is Outcome.Success -> {
               val server = serverRepository.findAll().first()

               when(val keys = generateClientKeys()) {
                  is Outcome.Failure -> {
                     CompletionError.printError(logger, keys)

                     2
                  }
                  is Outcome.Success -> {
                     val (privateKey, publicKey, presharedKey) = keys.value

                     val serverModel = server.toModel()
                     val peer = Peer(
                        id = Identifier.create(),
                        name = Text.create(username),
                        fourthOctet = fourthOctet.value,
                        privateKey = privateKey,
                        publicKey = publicKey,
                        presharedKey = presharedKey,
                        clientConfig = Text.create(""),
                        server = serverModel
                     )

                     val outStream = ByteArrayOutputStream()
                     val writer = OutputStreamWriter(outStream)

                     templatingService.renderClientConfig(peer, writer)

                     writer.flush()

                     peerRepository.save(PeerEntity(peer.copy(clientConfig = Text.create(outStream.toString(Charset.defaultCharset())))))

                     0
                  }
               }
            }
         }

      }

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
