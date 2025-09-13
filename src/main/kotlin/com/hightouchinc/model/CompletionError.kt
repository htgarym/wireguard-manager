package com.hightouchinc.model

sealed class CompletionError {
   class FirstThreeOctetsTooShort(val input: String): CompletionError()
   class FirstThreeOctetsNotFormattedCorrectly(val input: String): CompletionError()
   class PortTooLow(val input: Int): CompletionError()
   class PortTooHigh(val input: Int): CompletionError()
   class PortNotNumber(val input: String): CompletionError()
}
