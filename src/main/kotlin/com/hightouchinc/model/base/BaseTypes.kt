package com.hightouchinc.model.base

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

sealed interface Outcome<out T, out ERROR> {
   class Success<out T>(val value: T) : Outcome<T, Nothing>
   class Error<out ERROR>(val error: ERROR) : Outcome<Nothing, ERROR>
}

@JvmInline
value class Text private constructor(val value: String) {
   companion object {

      @JvmStatic
      fun create(value: String) = Text(value)
   }

   override fun toString(): String = value
}

@JvmInline
value class Identifier private constructor(val value: UUID) {
   constructor() :
      this(UuidCreator.getTimeOrderedEpoch())

   override fun toString(): String = value.toString()
}
