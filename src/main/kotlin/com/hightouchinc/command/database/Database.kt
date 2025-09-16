package com.hightouchinc.command.database

import io.micronaut.context.annotation.Value
import jakarta.inject.Inject
import java.util.concurrent.Callable
import picocli.CommandLine

@CommandLine.Command(
   name = "database",
   aliases = ["db"],
   description = ["Run queries on the database the easy way"],
   mixinStandardHelpOptions = true,
)
class Database @Inject constructor(
   @param:Value("\${datasources.default.url}") private val databaseUrl: String,
   @param:Value("\${datasources.default.username}") private val username: String,
): Callable<Int> {
   override fun call(): Int {
      org.h2.tools.Shell.main("-url", databaseUrl, "-user", username)

      return 0
   }
}
