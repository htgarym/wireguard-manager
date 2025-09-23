package com.hightouchinc.model

import com.hightouchinc.model.base.Outcome
import org.slf4j.Logger

sealed class CompletionError {
   class FirstThreeOctetsTooShort(val input: String): CompletionError()
   class FirstThreeOctetsTooLong(val input: String): CompletionError()
   class FirstThreeOctetsNotFormattedCorrectly(val input: String): CompletionError()
   class PortTooLow(val input: Int): CompletionError()
   class PortTooHigh(val input: Int): CompletionError()
   class PortNotNumber(val input: String): CompletionError()
   class UuidInvalid(val input: String, val message: String): CompletionError()
   class LoadingServerFromDbFailed(): CompletionError()
   class OctetTooLow(val input: Int): CompletionError()
   class OctetTooHigh(val input: Int): CompletionError()
   class NumberNotPositive(val input: Long): CompletionError()
   class IpV4AddressNotFormattedCorrectly(val input: String): CompletionError()
   class InvalidWireGuardKey(val input: String): CompletionError()
   class InvalidWireGuardPrivateKey(val input: String): CompletionError()
   class InvalidWireGuardPublicKey(val input: String): CompletionError()
   class InvalidWireGuardPresharedKey(val input: String): CompletionError()

   companion object {

      @JvmStatic
      fun printError(logger: Logger, completionErrorIn: Outcome.Failure<CompletionError>) =
         when (val completionError = completionErrorIn.error) {
            is FirstThreeOctetsNotFormattedCorrectly ->
               logger.error("First three octets are not formatted correctly: {}", completionError.input)
            is FirstThreeOctetsTooShort ->
               logger.error("First three octets are too short: {}", completionError.input)
            is FirstThreeOctetsTooLong ->
               logger.error("First three octets are too long: {}", completionError.input)
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
            is NumberNotPositive ->
               logger.error("Number was not positive: {}", completionError.input)
            is IpV4AddressNotFormattedCorrectly ->
               logger.error("IPv4 address was not formatted correctly: {}", completionError.input)
            is InvalidWireGuardKey ->
               logger.error("Invalid WireGuard key was not formatted correctly: {}", completionError.input)
            is InvalidWireGuardPrivateKey ->
               logger.error("Invalid WireGuard private key was not formatted correctly: {}", completionError.input)
            is InvalidWireGuardPublicKey ->
               logger.error("Invalid WireGuard public key was not formatted correctly: {}", completionError.input)
            is InvalidWireGuardPresharedKey -> 
               logger.error("Invalid WireGuard preshared key was not formatted correctly: {}", completionError.input)
         }
   }
}
