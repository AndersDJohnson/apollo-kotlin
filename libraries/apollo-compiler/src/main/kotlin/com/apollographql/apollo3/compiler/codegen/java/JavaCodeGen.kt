package com.apollographql.apollo3.compiler.codegen.java

import com.apollographql.apollo3.compiler.APOLLO_VERSION
import com.apollographql.apollo3.compiler.CodegenSchema
import com.apollographql.apollo3.compiler.JavaNullable
import com.apollographql.apollo3.compiler.JavaOperationsCodegenOptions
import com.apollographql.apollo3.compiler.JavaOutput
import com.apollographql.apollo3.compiler.JavaSchemaCodegenOptions
import com.apollographql.apollo3.compiler.MODELS_OPERATION_BASED
import com.apollographql.apollo3.compiler.PackageNameGenerator
import com.apollographql.apollo3.compiler.allTypes
import com.apollographql.apollo3.compiler.codegen.CodegenSymbols
import com.apollographql.apollo3.compiler.codegen.ResolverKey
import com.apollographql.apollo3.compiler.codegen.ResolverKeyKind
import com.apollographql.apollo3.compiler.codegen.java.adapter.EnumResponseAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.BuilderFactoryBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.EnumAsClassBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.EnumAsEnumBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.FragmentBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.FragmentDataAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.FragmentModelsBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.FragmentSelectionsBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.FragmentVariablesAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InputObjectAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InputObjectBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InterfaceBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InterfaceBuilderBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InterfaceMapBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.InterfaceUnknownMapBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.ObjectBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.ObjectBuilderBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.ObjectMapBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.OperationBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.OperationResponseAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.OperationSelectionsBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.OperationVariablesAdapterBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.ScalarBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.SchemaBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.UnionBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.UnionBuilderBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.UnionMapBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.UnionUnknownMapBuilder
import com.apollographql.apollo3.compiler.codegen.java.file.UtilAssertionsBuilder
import com.apollographql.apollo3.compiler.defaultClassesForEnumsMatching
import com.apollographql.apollo3.compiler.defaultDecapitalizeFields
import com.apollographql.apollo3.compiler.defaultGenerateFragmentImplementations
import com.apollographql.apollo3.compiler.defaultGenerateModelBuilders
import com.apollographql.apollo3.compiler.defaultGeneratePrimitiveTypes
import com.apollographql.apollo3.compiler.defaultGenerateQueryDocument
import com.apollographql.apollo3.compiler.defaultGenerateSchema
import com.apollographql.apollo3.compiler.defaultGeneratedSchemaName
import com.apollographql.apollo3.compiler.defaultNullableFieldStyle
import com.apollographql.apollo3.compiler.defaultUseSemanticNaming
import com.apollographql.apollo3.compiler.generateMethodsJava
import com.apollographql.apollo3.compiler.ir.DefaultIrOperations
import com.apollographql.apollo3.compiler.ir.DefaultIrSchema
import com.apollographql.apollo3.compiler.ir.IrOperations
import com.apollographql.apollo3.compiler.ir.IrSchema
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile

private class OutputBuilder {
  val builders = mutableListOf<JavaClassBuilder>()
}

private fun buildOutput(
    codegenSchema: CodegenSchema,
    upstreamCodegenSymbols: List<CodegenSymbols>,
    generatePrimitiveTypes: Boolean,
    nullableFieldStyle: JavaNullable,
    block: OutputBuilder.(resolver: JavaResolver) -> Unit,
): JavaOutput {

  val upstreamResolver = upstreamCodegenSymbols.fold(null as JavaResolver?) { acc, resolverInfo ->
    JavaResolver(
        entries = resolverInfo.entries,
        next = acc,
        scalarMapping = codegenSchema.scalarMapping,
        generatePrimitiveTypes = generatePrimitiveTypes,
        nullableFieldStyle = nullableFieldStyle,
    )
  }

  val resolver = JavaResolver(
      entries = emptyList(),
      next = upstreamResolver,
      scalarMapping = codegenSchema.scalarMapping,
      generatePrimitiveTypes = generatePrimitiveTypes,
      nullableFieldStyle = nullableFieldStyle,
  )

  val outputBuilder = OutputBuilder()
  outputBuilder.block(resolver)

  outputBuilder.builders.forEach { it.prepare() }
  val javaFiles = outputBuilder.builders
      .map {
        val codegenJavaFile = it.build()

        JavaFile.builder(
            codegenJavaFile.packageName,
            codegenJavaFile.typeSpec
        ).addFileComment(
            """
                
                AUTO-GENERATED FILE. DO NOT MODIFY.
                
                This class was automatically generated by Apollo GraphQL version '$APOLLO_VERSION'.
                
              """.trimIndent()
        )
            .build()
      }

  return JavaOutput(
      javaFiles,
      CodegenSymbols(
          entries = resolver.entries()
      )
  )
}

object JavaCodegen {
  fun buildOperations(
      codegenSchema: CodegenSchema,
      irOperations: IrOperations,
      upstreamCodegenSymbols: List<CodegenSymbols>,
      javaOperationsCodegenOptions: JavaOperationsCodegenOptions,
      packageNameGenerator: PackageNameGenerator,
  ): JavaOutput {
    check(irOperations is DefaultIrOperations)

    if (irOperations.codegenModels != MODELS_OPERATION_BASED) {
      error("Java codegen does not support ${irOperations.codegenModels}. Only $MODELS_OPERATION_BASED is supported.")
    }
    if (!irOperations.flattenModels) {
      error("Java codegen does not support nested models as it could trigger name clashes when a nested class has the same name as an " +
          "enclosing one.")
    }

    val flatten = irOperations.flattenModels
    val generateDataBuilders = irOperations.generateDataBuilders
    val decapitalizeFields = irOperations.decapitalizeFields

    val generateFragmentImplementations = javaOperationsCodegenOptions.generateFragmentImplementations
        ?: defaultGenerateFragmentImplementations
    val generateQueryDocument = javaOperationsCodegenOptions.generateQueryDocument ?: defaultGenerateQueryDocument
    val useSemanticNaming = javaOperationsCodegenOptions.useSemanticNaming ?: defaultUseSemanticNaming

    val generateModelBuilders = javaOperationsCodegenOptions.generateModelBuilders ?: defaultGenerateModelBuilders

    val generateMethods = generateMethodsJava(javaOperationsCodegenOptions.generateMethods)
    val generatePrimitiveTypes = javaOperationsCodegenOptions.generatePrimitiveTypes ?: defaultGeneratePrimitiveTypes
    val nullableFieldStyle = javaOperationsCodegenOptions.nullableFieldStyle ?: defaultNullableFieldStyle

    return buildOutput(
        codegenSchema = codegenSchema,
        upstreamCodegenSymbols = upstreamCodegenSymbols,
        generatePrimitiveTypes = generatePrimitiveTypes,
        nullableFieldStyle = nullableFieldStyle
    ) { resolver ->
      val layout = JavaOperationsCodegenLayout(
          allTypes = codegenSchema.allTypes(),
          useSemanticNaming = useSemanticNaming,
          packageNameGenerator = packageNameGenerator,
      )

      val context = JavaOperationsContext(
          layout = layout,
          resolver = resolver,
          generateMethods = generateMethodsJava(generateMethods),
          generateModelBuilders = generateModelBuilders,
          nullableFieldStyle = nullableFieldStyle,
          decapitalizeFields = decapitalizeFields
      )

      irOperations.fragments
          .forEach { fragment ->
            builders.add(
                FragmentModelsBuilder(
                    context,
                    fragment,
                    (fragment.interfaceModelGroup ?: fragment.dataModelGroup),
                    fragment.interfaceModelGroup == null,
                    flatten,
                )
            )

            builders.add(FragmentSelectionsBuilder(context, fragment))

            if (generateFragmentImplementations || fragment.interfaceModelGroup == null) {
              builders.add(FragmentDataAdapterBuilder(context, fragment, flatten))
            }

            if (generateFragmentImplementations) {
              builders.add(
                  FragmentBuilder(
                      context,
                      fragment,
                      flatten,
                  )
              )
              if (fragment.variables.isNotEmpty()) {
                builders.add(FragmentVariablesAdapterBuilder(context, fragment))
              }
            }
          }

      irOperations.operations
          .forEach { operation ->
            if (operation.variables.isNotEmpty()) {
              builders.add(OperationVariablesAdapterBuilder(context, operation))
            }

            builders.add(OperationSelectionsBuilder(context, operation))
            builders.add(OperationResponseAdapterBuilder(context, operation, flatten))

            builders.add(
                OperationBuilder(
                    context = context,
                    operationId = operation.id,
                    generateQueryDocument = generateQueryDocument,
                    operation = operation,
                    flatten = flatten,
                    generateDataBuilders = generateDataBuilders
                )
            )
          }
    }
  }

  fun buildSchema(
      codegenSchema: CodegenSchema,
      irSchema: IrSchema,
      options: JavaSchemaCodegenOptions,
  ): JavaOutput {
    check (irSchema is DefaultIrSchema)

    val generateSchema = options.generateSchema ?: defaultGenerateSchema || codegenSchema.generateDataBuilders
    val generatedSchemaName = options.generatedSchemaName ?: defaultGeneratedSchemaName
    val classesForEnumsMatching = options.classesForEnumsMatching ?: defaultClassesForEnumsMatching

    val generatePrimitiveTypes = options.generatePrimitiveTypes ?: defaultGeneratePrimitiveTypes
    val nullableFieldStyle = options.nullableFieldStyle ?: defaultNullableFieldStyle

    val generateMethods = generateMethodsJava(options.generateMethods)
    val generateDataBuilders = codegenSchema.generateDataBuilders
    val scalarMapping = codegenSchema.scalarMapping
    val decapitalizeFields = options.decapitalizeFields ?: defaultDecapitalizeFields

    return buildOutput(
        codegenSchema,
        emptyList(),
        generatePrimitiveTypes,
        nullableFieldStyle
    ) { resolver ->

      val layout = JavaSchemaCodegenLayout(
          allTypes = codegenSchema.allTypes(),
          schemaPackageName = codegenSchema.packageName,
      )

      val context = JavaSchemaContext(
          layout = layout,
          resolver = resolver,
          generateMethods = generateMethodsJava(generateMethods),
          nullableFieldStyle = nullableFieldStyle,
          decapitalizeFields = decapitalizeFields
      )

      irSchema.irScalars.forEach { irScalar ->
        builders.add(ScalarBuilder(context, irScalar, scalarMapping.get(irScalar.name)?.targetName))
      }
      irSchema.irEnums.forEach { irEnum ->
        if (classesForEnumsMatching.any { Regex(it).matches(irEnum.name) }) {
          builders.add(EnumAsClassBuilder(context, irEnum))
        } else {
          builders.add(EnumAsEnumBuilder(context, irEnum))
        }
        builders.add(EnumResponseAdapterBuilder(context, irEnum))
      }
      irSchema.irInputObjects.forEach { irInputObject ->
        builders.add(InputObjectBuilder(context, irInputObject))
        builders.add(InputObjectAdapterBuilder(context, irInputObject))
      }
      if (context.nullableFieldStyle == JavaNullable.GUAVA_OPTIONAL && irSchema.irInputObjects.any { it.isOneOf }) {
        // When using the Guava optionals, generate assertOneOf in the project, as apollo-api doesn't depend on Guava
        builders.add(UtilAssertionsBuilder(context))
      }
      irSchema.irUnions.forEach { irUnion ->
        builders.add(UnionBuilder(context, irUnion))
        if (generateDataBuilders) {
          builders.add(UnionBuilderBuilder(context, irUnion))
          builders.add(UnionUnknownMapBuilder(context, irUnion))
          builders.add(UnionMapBuilder(context, irUnion))
        }
      }
      irSchema.irInterfaces.forEach { irInterface ->
        builders.add(InterfaceBuilder(context, irInterface))
        if (generateDataBuilders) {
          builders.add(InterfaceBuilderBuilder(context, irInterface))
          builders.add(InterfaceUnknownMapBuilder(context, irInterface))
          builders.add(InterfaceMapBuilder(context, irInterface))
        }
      }
      irSchema.irObjects.forEach { irObject ->
        builders.add(ObjectBuilder(context, irObject))
        if (generateDataBuilders) {
          builders.add(ObjectBuilderBuilder(context, irObject))
          builders.add(ObjectMapBuilder(context, irObject))
        }
      }
      if (generateSchema && context.resolver.resolve(ResolverKey(ResolverKeyKind.Schema, "")) == null) {
        builders.add(SchemaBuilder(context, generatedSchemaName, scalarMapping, irSchema.irObjects, irSchema.irInterfaces, irSchema.irUnions, irSchema.irEnums))
      }
      if (generateDataBuilders) {
        builders.add(BuilderFactoryBuilder(context, irSchema.irObjects, irSchema.irInterfaces, irSchema.irUnions))
      }
    }
  }
}

internal fun List<CodeBlock>.joinToCode(separator: String, prefix: String = "", suffix: String = ""): CodeBlock {
  var first = true
  return fold(
      CodeBlock.builder().add(prefix)
  ) { builder, block ->
    if (first) {
      first = false
    } else {
      builder.add(separator)
    }
    builder.add(L, block)
  }.add(suffix)
      .build()
}

internal fun CodeBlock.isNotEmpty() = isEmpty().not()

internal const val T = "${'$'}T"
internal const val L = "${'$'}L"
internal const val S = "${'$'}S"
