package com.hightouchinc.model

import com.hightouchinc.model.base.Outcome
import org.apache.commons.codec.binary.Base64

@JvmInline
value class WireGuardKey private constructor(val value: String) {
   companion object {

      @JvmStatic
      fun create(keyText: String): Outcome<WireGuardKey, CompletionError> =
         if(Base64.isBase64(keyText)) {
            Outcome.Success(WireGuardKey(keyText))
         } else {
            Outcome.Failure(CompletionError.InvalidWireGuardKey(keyText))
         }
   }
}

typealias PublicWireGuardKey = WireGuardKey
typealias PrivateWireGuardKey = WireGuardKey
typealias PresharedWireGuardKey = WireGuardKey
