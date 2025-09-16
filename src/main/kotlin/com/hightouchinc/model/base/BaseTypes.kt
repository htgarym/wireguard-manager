package com.hightouchinc.model.base

import com.github.f4b6a3.uuid.UuidCreator
import com.hightouchinc.model.CompletionError
import java.util.UUID
import org.apache.commons.lang3.StringUtils

sealed interface Outcome<out T, out ERROR> {
   class Success<out T>(val value: T) : Outcome<T, Nothing>
   class Failure<out ERROR>(val error: ERROR) : Outcome<Nothing, ERROR>
}

@JvmInline
value class Text private constructor(val value: String) {
   companion object {
      @JvmStatic
      private val empty = Text(StringUtils.EMPTY)

      @JvmStatic
      fun create(value: String) = Text(value)

      @JvmStatic
      fun createEmpty() = empty
   }

   override fun toString(): String = value
}

@JvmInline
value class Identifier private constructor(val value: UUID) {
   constructor() :
      this(UuidCreator.getTimeOrderedEpoch())

   companion object Companion {

      @JvmStatic
      fun create() = Identifier()

      @JvmStatic
      fun create(value: UUID) = Identifier(value)

      @JvmStatic
      fun create(id: String): Outcome<Identifier, CompletionError> =
         try {
            Outcome.Success(Identifier(UUID.fromString(id)))
         } catch (e: IllegalArgumentException) {
            Outcome.Failure(CompletionError.UuidInvalid(id, e.localizedMessage))
         }
   }

   override fun toString(): String = value.toString()
}
