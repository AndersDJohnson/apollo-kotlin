package com.apollographql.apollo3.compiler.codegen

import com.apollographql.apollo3.annotations.ApolloInternal
import kotlinx.serialization.Serializable


/**
 * Additional resolver data generated alongside the models and adapters.
 * This data maps a GraphQL identifier (such as a typename or model path) to its target class.
 * This allows children modules to reference classes generated in parents (and know when to skip generating them).
 */
@Serializable
class CodegenSymbols(
    val entries: List<ResolverEntry>
) {
  operator fun plus(other: CodegenSymbols): CodegenSymbols {
    return CodegenSymbols(entries + other.entries)
  }

  companion object {
    val Empty = CodegenSymbols(emptyList())
  }
}

@Serializable
@ApolloInternal
class ResolverClassName(val packageName: String, val simpleNames: List<String>) {
  constructor(packageName: String, vararg simpleNames: String): this(packageName, simpleNames.toList())
}

/**
 * Must be a data class because it is used as a key in resolvers
 */
@Serializable
data class ResolverKey(val kind: ResolverKeyKind, val id: String)

enum class ResolverKeyKind {
  SchemaType,
  Model,
  SchemaTypeAdapter,
  ModelAdapter,
  Operation,
  OperationVariablesAdapter,
  OperationSelections,
  Fragment,
  FragmentVariablesAdapter,
  FragmentSelections,
  MapType,
  BuilderType,
  BuilderFun,
  Schema,
  CustomScalarAdapters,
  Pagination,
}

@Serializable
class ResolverEntry(
    val key: ResolverKey,
    val className: ResolverClassName
)

@ApolloInternal
fun CodegenSymbols.resolveSchemaType(name: String): ResolverClassName? {
  return (this as CodegenSymbols).entries.firstOrNull { it.key.kind == ResolverKeyKind.SchemaType && it.key.id == name }?.className
}
