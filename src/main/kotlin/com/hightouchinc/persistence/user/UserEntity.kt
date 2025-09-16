package com.hightouchinc.persistence.user

import com.hightouchinc.model.User
import com.hightouchinc.persistence.server.ServerEntity
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
@MappedEntity("users")
data class UserEntity(

   @field:Id
   val id: UUID,

   val name: String,

   val fourthOctet: Int,

   @field:Relation(value = MANY_TO_ONE)
   val server: ServerEntity,

) {
   constructor(user: User):
      this(
         id = user.id.value,
         name = user.name.toString(),
         fourthOctet = user.fourthOctet.value,
         server = ServerEntity(user.server),
      )
}
