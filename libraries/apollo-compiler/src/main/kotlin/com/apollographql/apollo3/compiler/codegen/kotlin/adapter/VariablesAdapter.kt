package com.apollographql.apollo3.compiler.codegen.kotlin.adapter

import com.apollographql.apollo3.compiler.codegen.Identifier.customScalarAdapters
import com.apollographql.apollo3.compiler.codegen.Identifier.serializeVariables
import com.apollographql.apollo3.compiler.codegen.Identifier.value
import com.apollographql.apollo3.compiler.codegen.Identifier.withDefaultValues
import com.apollographql.apollo3.compiler.codegen.Identifier.writer
import com.apollographql.apollo3.compiler.codegen.kotlin.KotlinOperationsContext
import com.apollographql.apollo3.compiler.codegen.kotlin.KotlinSymbols
import com.apollographql.apollo3.compiler.codegen.kotlin.helpers.codeBlock
import com.apollographql.apollo3.compiler.codegen.kotlinPropertyName
import com.apollographql.apollo3.compiler.ir.IrVariable
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

internal fun List<IrVariable>.variablesAdapterTypeSpec(
    context: KotlinOperationsContext,
    adapterName: String,
    adaptedTypeName: TypeName,
): TypeSpec {
  return TypeSpec.objectBuilder(adapterName)
      .addFunction(serializeVariablesFunSpec(context, adaptedTypeName))
      .build()
}

private fun List<IrVariable>.serializeVariablesFunSpec(
    context: KotlinOperationsContext,
    adaptedTypeName: TypeName,
): FunSpec {
  return FunSpec.builder(serializeVariables)
      .addParameter(writer, KotlinSymbols.JsonWriter)
      .addParameter(value, adaptedTypeName)
      .addParameter(customScalarAdapters, KotlinSymbols.CustomScalarAdapters)
      .addParameter(withDefaultValues, KotlinSymbols.Boolean)
      .addAnnotation(AnnotationSpec.builder(KotlinSymbols.Suppress).addMember("%S", "UNUSED_PARAMETER").addMember("%S", "UNUSED_VARIABLE").build())
      .addCode(writeToResponseCodeBlock(context))
      .build()
}

private fun List<IrVariable>.writeToResponseCodeBlock(context: KotlinOperationsContext): CodeBlock {
  val builder = CodeBlock.builder()

  forEach {
    builder.add(it.writeToResponseCodeBlock(context))
  }
  return builder.build()
}

private fun IrVariable.writeToResponseCodeBlock(context: KotlinOperationsContext): CodeBlock {
  val adapterInitializer = context.resolver.adapterInitializer(type, false, context.jsExport, customScalarAdapters)
  val builder = CodeBlock.builder()
  val propertyName = context.kotlinPropertyName(name)

  if (type.optional) {
    builder.beginControlFlow("if ($value.%N is %T)", propertyName, KotlinSymbols.Present)
  }
  builder.addStatement("$writer.name(%S)", name)
  builder.addSerializeStatement(adapterInitializer, propertyName)
  if (type.optional) {
    builder.endControlFlow()
    if (defaultValue != null) {
      builder.beginControlFlow("else if ($withDefaultValues)")
      builder.addStatement("$writer.name(%S)", name)
      builder.addStatement(
          "%M.toJson($writer, $customScalarAdapters, %L)",
          KotlinSymbols.NullableAnyAdapter,
          defaultValue.codeBlock(),
      )

      builder.endControlFlow()
    }
  }

  return builder.build()
}
