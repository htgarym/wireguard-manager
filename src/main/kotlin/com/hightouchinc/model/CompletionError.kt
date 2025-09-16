package com.hightouchinc.model

import org.slf4j.Logger

sealed class CompletionError {
   class FirstThreeOctetsTooShort(val input: String): CompletionError()
   class FirstThreeOctetsNotFormattedCorrectly(val input: String): CompletionError()
   class PortTooLow(val input: Int): CompletionError()
   class PortTooHigh(val input: Int): CompletionError()
   class PortNotNumber(val input: String): CompletionError()
   class UuidInvalid(val input: String, val message: String): CompletionError()
   class LoadingServerFromDbFailed(): CompletionError()
   class OctetTooLow(val input: Int): CompletionError()
   class OctetTooHigh(val input: Int): CompletionError()

   companion object {

      @JvmStatic
      fun printError(logger: Logger, completionError: CompletionError) =
         when (completionError) {
            is FirstThreeOctetsNotFormattedCorrectly ->
               logger.error("First three octets is not correctly formatted correctly: {}", completionError.input)
            is FirstThreeOctetsTooShort ->
               logger.error("first three octets is too short : {}", completionError.input)
            is PortNotNumber ->
               logger.error("Port was not a number: {}", completionError.input)
            is PortTooHigh ->
               logger.error("Port number was too high: {}", completionError.input)
            is PortTooLow ->
               logger.error("Port was too low: {}", completionError.input)
            is UuidInvalid ->
               logger.error("UUID is not a valid UUID: {} -> {}", completionError.input, completionError.message)
            is LoadingServerFromDbFailed ->
               logger.error("Unable to load server definition from database")
            is OctetTooLow ->
               logger.error("Fourth octet was too low: {}", completionError.input)
            is OctetTooHigh ->
               logger.error("Fourth octet was too high: {}", completionError.input)
         }
   }
}

