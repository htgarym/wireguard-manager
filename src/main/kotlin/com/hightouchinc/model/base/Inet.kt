package com.hightouchinc.model.base

import com.hightouchinc.model.CompletionError
import org.apache.commons.lang3.StringUtils

@JvmInline
value class FirstThreeOctets private constructor(val value: String) {
   companion object {
      private val regex =
         """^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex()

      @JvmStatic
      fun create(octets: String): Outcome<FirstThreeOctets, CompletionError> =
         when {
            octets.length > 11 -> Outcome.Error(CompletionError.FirstThreeOctetsTooShort(octets))
            !regex.matches(octets) -> Outcome.Error(CompletionError.FirstThreeOctetsNotFormattedCorrectly(octets))
            else -> Outcome.Success(FirstThreeOctets(octets))
         }
   }

   override fun toString(): String = value
}

@JvmInline
value class Port private constructor(val value: Int) {
   companion object {

      @JvmStatic
      fun create(port: Int): Outcome<Port, CompletionError> =
         when {
            port < 1 -> Outcome.Error(CompletionError.PortTooLow(port))
            port > 65_535 -> Outcome.Error(CompletionError.PortTooHigh(port))
            else -> Outcome.Success(Port(port))
         }

      @JvmStatic
      fun create(port: String): Outcome<Port, CompletionError> =
         when {
            StringUtils.isNumeric(port) -> create(Integer.parseInt(port))
            else -> Outcome.Error(CompletionError.PortNotNumber(port))
         }
   }

   override fun toString(): String = value.toString()
}
