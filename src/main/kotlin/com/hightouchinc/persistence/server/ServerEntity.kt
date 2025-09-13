package com.hightouchinc.persistence.server

import com.hightouchinc.model.Server
import com.hightouchinc.model.base.FirstThreeOctets
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Outcome
import com.hightouchinc.model.base.Port
import com.hightouchinc.model.base.Text
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.serde.annotation.Serdeable
import java.util.*

@Serdeable
@MappedEntity("servers")
data class ServerEntity(

   @field:Id
   val id: UUID,

   @field:MappedProperty("interface")
   val iface: String,
   val firstThreeOctets: String,
   val port: Int,
) {
   constructor(server: Server) :
      this(
         id = server.id.value,
         iface = server.iface.toString(),
         firstThreeOctets = server.firstThreeOctets.toString(),
         port = server.port.value,
      )

   fun toModel() = Server(
      id = Identifier.create(id),
      iface = Text.create(iface),
      firstThreeOctets = when(val fto = FirstThreeOctets.create(firstThreeOctets)) {
         is Outcome.Success -> fto.value
         is Outcome.Error -> throw IllegalStateException("Invalid FirstThreeOctets in DB: $firstThreeOctets")
      },
      port = when(val port = Port.create(port)) {
         is Outcome.Success -> port.value
         is Outcome.Error -> throw IllegalStateException("Invalid Port in DB: $port")
      },
   )
}
