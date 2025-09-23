package com.hightouchinc.persistence.peer

import com.hightouchinc.model.Peer
import com.hightouchinc.model.PresharedWireGuardKey
import com.hightouchinc.model.PrivateWireGuardKey
import com.hightouchinc.model.PublicWireGuardKey
import com.hightouchinc.model.base.FourthOctet
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Text
import com.hightouchinc.persistence.server.ServerEntity
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("peer")
data class PeerEntity(
   @field:Id val id: UUID,
   val name: String,
   val fourthOctet: Int,
   val privateKey: String,
   val publicKey: String,
   val presharedKey: String,
   val peerConfig: String,
   @field:Relation(value = MANY_TO_ONE) val server: ServerEntity,
) {

   constructor(peer: Peer):
      this(
         id = peer.id.value,
         name = peer.name.toString(),
         fourthOctet = peer.fourthOctet.value,
         privateKey = peer.privateKey.value,
         publicKey = peer.publicKey.value,
         presharedKey = peer.presharedKey.value,
         peerConfig = peer.clientConfig.value,
         server = ServerEntity(peer.server),
      )

   fun toModel(): Peer =
      Peer(
         id = Identifier.create(id),
         name = Text.create(name),
         fourthOctet = when(val fourthOctet = FourthOctet.create(fourthOctet)) {
            is Outcome.Success -> fourthOctet.value
            is Outcome.Failure -> throw IllegalStateException("Invalid fourth octet in database: ${fourthOctet.error}")
         },
         privateKey = when(val privateKey = PrivateWireGuardKey.create(privateKey)) {
            is Outcome.Success -> privateKey.value
            is Outcome.Failure -> throw IllegalStateException("Invalid private key in database: ${privateKey.error}")
         },
         publicKey = when(val publicKey = PublicWireGuardKey.create(publicKey)) {
            is Outcome.Success -> publicKey.value
            is Outcome.Failure -> throw IllegalStateException("Invalid public key in database: ${publicKey.error}")
         },
         presharedKey = when(val presharedKey = PresharedWireGuardKey.create(presharedKey)) {
            is Outcome.Success -> presharedKey.value
            is Outcome.Failure -> throw IllegalStateException("Invalid preshared key in database: ${presharedKey.error}")
         },
         clientConfig = Text.create(peerConfig),
         server = server.toModel()
      )
}
