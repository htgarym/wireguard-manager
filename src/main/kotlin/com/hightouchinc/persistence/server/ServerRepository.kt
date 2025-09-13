package com.hightouchinc.persistence.server

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.ANSI
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = ANSI)
interface ServerRepository: CrudRepository<ServerEntity, String>
