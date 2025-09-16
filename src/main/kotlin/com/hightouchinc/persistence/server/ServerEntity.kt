package com.hightouchinc.persistence.server

import com.hightouchinc.model.PrivateWireGuardKey
import com.hightouchinc.model.PublicWireGuardKey
import com.hightouchinc.model.Server
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.IpV4Address
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Text
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.util.*

@Serdeable
@MappedEntity("server")
data class ServerEntity(
   @field:Id val id: UUID,
   val iface: String,
   val publicKey: String,
   val privateKey: String,
   val publicAddress: String,
) {
   constructor(server: Server) :
      this(
         id = server.id.value,
         iface = server.iface.value,
         publicKey = server.publicKey.value,
         privateKey = server.privateKey.value,
         publicAddress = server.publicAddress.value,
      )

   fun toModel(): Server =
      Server(
         id = Identifier.create(id),
         iface = Text.create(iface),
         publicKey = when(val publicKey = PublicWireGuardKey.create(publicKey)) {
            is Outcome.Success -> publicKey.value
            is Outcome.Failure -> throw IllegalStateException("Invalid public key in database: ${publicKey.error}")
         },
         privateKey = when(val privateKey = PrivateWireGuardKey.create(privateKey)) {
            is Outcome.Success -> privateKey.value
            is Outcome.Failure -> throw IllegalStateException("Invalid private key in database: ${privateKey.error}")
         },
         publicAddress = when(val publicAddress = IpV4Address.create(publicAddress)) {
            is Outcome.Success -> publicAddress.value
            is Outcome.Failure -> throw IllegalStateException("Invalid public address in database: ${publicAddress.error}")
         },
      )
}
