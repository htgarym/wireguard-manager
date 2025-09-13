package com.hightouchinc.persistence.server

import com.hightouchinc.model.Server
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@MappedEntity("server")
data class ServerEntity(

   @field:Id
   val id: String,

   @field:MappedProperty("interface")
   val iface: String,
   val firstThreeOctets: String,
   val port: Int,
) {
   constructor(server: Server) :
      this(
         id = server.id.toString(),
         iface = server.iface.toString(),
         firstThreeOctets = server.firstThreeOctets.toString(),
         port = server.port.value,
      )
}
