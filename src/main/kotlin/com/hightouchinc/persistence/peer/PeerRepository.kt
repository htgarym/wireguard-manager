package com.hightouchinc.persistence.peer

import io.micronaut.data.annotation.Join
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.H2
import io.micronaut.data.repository.PageableRepository
import java.util.*

@JdbcRepository(dialect = H2)
interface PeerRepository: PageableRepository<PeerEntity, UUID> {
   @Join("server")
   fun findByServerId(serverId: UUID): List<PeerEntity>
}
