package com.hightouchinc.persistence.user

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect.H2
import io.micronaut.data.repository.PageableRepository
import java.util.*

@JdbcRepository(dialect = H2)
interface UserRepository: PageableRepository<UserEntity, UUID>
