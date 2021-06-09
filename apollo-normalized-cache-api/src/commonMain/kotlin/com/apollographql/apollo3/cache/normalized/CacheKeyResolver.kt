package com.apollographql.apollo3.cache.normalized

import com.apollographql.apollo3.api.CompiledField
import com.apollographql.apollo3.api.Executable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSuppressWildcards

/**
 * Resolves a cache key for a JSON object.
 */
abstract class CacheKeyResolver {
  abstract fun fromFieldRecordSet(
      field: CompiledField,
      variables: Executable.Variables,
      recordSet: Map<String, @JvmSuppressWildcards Any?>
  ): CacheKey?

  abstract fun fromFieldArguments(
      field: CompiledField,
      variables: Executable.Variables
  ): CacheKey?

  companion object {
    private val ROOT_CACHE_KEY = CacheKey("QUERY_ROOT")

    @JvmField
    val DEFAULT: CacheKeyResolver = object : CacheKeyResolver() {
      override fun fromFieldRecordSet(field: CompiledField, variables: Executable.Variables, recordSet: Map<String, Any?>): CacheKey? = null

      override fun fromFieldArguments(field: CompiledField, variables: Executable.Variables): CacheKey? = null
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun rootKey(): CacheKey {
      return ROOT_CACHE_KEY
    }
  }
}
