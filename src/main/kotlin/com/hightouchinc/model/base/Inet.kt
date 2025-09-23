package com.hightouchinc.model.base

import com.hightouchinc.model.CompletionError
import org.apache.commons.lang3.StringUtils
import org.apache.commons.validator.routines.InetAddressValidator

@JvmInline
value class FirstThreeOctets private constructor(val value: String) {
   companion object {
      private val regex =
         """^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex()

      @JvmStatic
      fun create(octets: String): Outcome<FirstThreeOctets, CompletionError> =
         when {
            octets.length < 5 -> Outcome.Failure(CompletionError.FirstThreeOctetsTooShort(octets))
            octets.length > 11 -> Outcome.Failure(CompletionError.FirstThreeOctetsTooLong(octets))
            !regex.matches(octets) -> Outcome.Failure(CompletionError.FirstThreeOctetsNotFormattedCorrectly(octets))
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
            port < 1 -> Outcome.Failure(CompletionError.PortTooLow(port))
            port > 65_535 -> Outcome.Failure(CompletionError.PortTooHigh(port))
            else -> Outcome.Success(Port(port))
         }

      @JvmStatic
      fun create(port: String): Outcome<Port, CompletionError> =
         when {
            StringUtils.isNumeric(port) -> create(Integer.parseInt(port))
            else -> Outcome.Failure(CompletionError.PortNotNumber(port))
         }
   }

   override fun toString(): String = value.toString()
}

@JvmInline
value class FourthOctet private constructor(val value: Int) {
   companion object {

      @JvmStatic
      fun create(octet: Int): Outcome<FourthOctet, CompletionError> =
         when {
            octet < 1 -> Outcome.Failure(CompletionError.OctetTooLow(octet))
            octet > 254 -> Outcome.Failure(CompletionError.OctetTooHigh(octet))
            else -> Outcome.Success(FourthOctet(octet))
         }
   }

   override fun toString(): String = value.toString()
}

@JvmInline
value class IpV4Address private constructor(val value: String) {
   companion object {
      @JvmStatic
      private val validator = InetAddressValidator.getInstance()

      @JvmStatic
      fun create(address: String): Outcome<IpV4Address, CompletionError> =
         if(validator.isValidInet4Address(address)) {
            Outcome.Success(IpV4Address(address))
         } else {
            Outcome.Failure(CompletionError.IpV4AddressNotFormattedCorrectly(address))
         }
   }
}
