package com.hightouchinc.command.user

import com.hightouchinc.model.User
import com.hightouchinc.model.base.FourthOctet
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.persistence.server.ServerEntity
import com.hightouchinc.persistence.server.ServerRepository
import com.hightouchinc.persistence.user.UserEntity
import com.hightouchinc.persistence.user.UserRepository
import jakarta.inject.Inject
import java.util.concurrent.Callable
import org.jline.consoleui.prompt.ConsolePrompt
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(
   name = "add",
   description = ["Add a user to the server"],
   mixinStandardHelpOptions = true,
)
class UserAddCommand @Inject constructor(
   private val serverRepository: ServerRepository,
   private val userRepository: UserRepository,
): Callable<Int>, CommandLine.IExitCodeGenerator {
   private val logger = LoggerFactory.getLogger(UserAddCommand::class.java)
   private var exitCode = 0

   @CommandLine.Parameters(arity = "0", paramLabel = "USER", description = ["Username"])
   lateinit var username: String

   @CommandLine.Parameters(arity = "1", paramLabel = "FOURTH_OCTET", description = ["Fourth Octet"], defaultValue = "2")
   var fourthOctetIn: Int = 0

   override fun call(): Int {
      when (val fourthOctet = FourthOctet.create(fourthOctetIn)) {
         is Outcome.Error -> {
            logger.error("Fourth octet is not valid: {}", fourthOctet.error)
            exitCode = 1
         }
         is Outcome.Success -> {
            val servers = serverRepository.findAll()
            val promptResult = TerminalBuilder.builder().system(true).build().use { terminal ->
               val prompt = ConsolePrompt(terminal)

               prompt.prompt(
                  prompt.promptBuilder
                     .buildServerPrompt("server", servers.map(ServerEntity::toModel))
                     .build(),
               )
            }

            val serverId = promptResult["server"]?.result
            logger.debug("Server selected: {}", serverId)

            val server = servers.find { it.id.toString() == serverId }

            if (server != null) {
               logger.debug("Server found: {}", server)
               val user = User.create(username, fourthOctet.value, server.toModel())

               userRepository.save(UserEntity(user))

               logger.info("User saved: {}", user)
            } else {
               logger.error("Server not found not saving user: {}->{}", username, fourthOctetIn)
            }
         }
      }

      return exitCode
   }

   override fun getExitCode(): Int = exitCode
}
