package com.hightouchinc.model

import com.hightouchinc.model.base.FourthOctet
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Text

data class User(
   val id: Identifier,
   val name: Text,
   val server: Server,
   val fourthOctet: FourthOctet,
) {
   companion object {

      @JvmStatic
      fun create(name: String, fourthOctet: FourthOctet, server: Server) =
         User(
            id = Identifier(),
            name = Text.create(name),
            server = server,
            fourthOctet = fourthOctet,
         )

      @JvmStatic
      fun create(name: String, fourthOctetIn: Int, server: Server): Outcome<User, CompletionError> =
         when (val fourthOctet = FourthOctet.create(fourthOctetIn)) {
            is Outcome.Error -> Outcome.Error(fourthOctet.error)
            is Outcome.Success -> Outcome.Success(
               create(name, fourthOctet.value, server),
            )
         }
   }
}
