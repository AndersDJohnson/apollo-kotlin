package com.apollographql.apollo3.compiler.codegen

import com.apollographql.apollo3.compiler.CodegenSchema
import com.apollographql.apollo3.compiler.PackageNameGenerator
import com.apollographql.apollo3.compiler.allTypes
import com.apollographql.apollo3.compiler.capitalizeFirstLetter
import com.apollographql.apollo3.compiler.decapitalizeFirstLetter
import com.apollographql.apollo3.compiler.internal.singularize
import com.apollographql.apollo3.compiler.ir.IrFieldInfo
import com.apollographql.apollo3.compiler.ir.IrListType
import com.apollographql.apollo3.compiler.ir.IrOperation
import com.apollographql.apollo3.compiler.ir.IrType
import com.apollographql.apollo3.compiler.ir.TypeSet
import com.apollographql.apollo3.compiler.maybeAddSuffix
import com.apollographql.apollo3.compiler.uniqueName
import com.apollographql.apollo3.compiler.upperCamelCaseIgnoringNonLetters
import com.apollographql.apollo3.compiler.withUnderscorePrefix

/**
 * The central place where the names/packages of the different classes are decided and escape rules done.
 *
 * Inputs should always be GraphQL identifiers and outputs are valid Kotlin/Java identifiers.
 */
internal class SchemaAndOperationsLayoutImpl(
    codegenSchema: CodegenSchema,
    private val packageNameGenerator: PackageNameGenerator,
    private val useSemanticNaming: Boolean,
    private val decapitalizeFields: Boolean
) : SchemaAndOperationsLayout {
  private val schemaPackageName = executableDocumentPackageName(codegenSchema.normalizedPath)
  private val schemaTypeToClassName: Map<String, String> = mutableMapOf<String, String>().apply {
    val usedNames = mutableSetOf<String>()
    val allTypes = codegenSchema.allTypes()

    /**
     * Make it possible to support several types with different cases. Example:
     *
     * type URL @targetName(newName: "Url1")
     * type Url
     * type url
     *
     * Because we capitalize the first letter, we need to escape the name because else `Url` and `url` clash
     */
    // 1. Compute a unique name for types without a targetName
    for (type in allTypes.filter { it.targetName == null }) {
      val uniqueName = uniqueName(type.name, usedNames)
      val className = uniqueName.capitalizeFirstLetter()

      usedNames.add(className.lowercase())
      this[type.name] = className
    }

    // 2. Use targetName verbatim for types that define it
    for (type in allTypes.filter { it.targetName != null }) {
      this[type.name] = type.targetName!!
    }
  }

  override fun schemaPackageName(): String = schemaPackageName

  override fun executableDocumentPackageName(filePath: String?): String = packageNameGenerator.packageName(filePath ?: "")

  override fun schemaTypeName(schemaTypeName: String): String {
    return schemaTypeToClassName[schemaTypeName]?.let {
      topLevelName(it)
    } ?: error("unknown schema type: $schemaTypeName")
  }

  override fun operationName(name: String, capitalizedOperationType: String): String {
    return topLevelName(name).let {
      if (useSemanticNaming) {
        it.maybeAddSuffix(capitalizedOperationType)
      } else {
        it
      }
    }
  }

  override fun fragmentName(name: String): String {
    return topLevelName(name)
  }

  override fun topLevelName(name: String): String {
    return name.capitalizeFirstLetter()
  }

  override fun propertyName(name: String) = if (decapitalizeFields) name.decapitalizeFirstLetter() else name
}

private fun IrType.isList(): Boolean {
  return when (this) {
    is IrListType -> true
    else -> false
  }
}

/**
 * Build a name for a model
 *
 * @param info the field info, including responseName and whether this field is of list type. If the field is of
 * list type, the model name will be singularized
 * @param typeSet the different type conditions for this model
 * @param rawTypename the type of the field. Because it is always satisfied, it is not included in the model name
 * @param isOther whether this is a fallback type
 */
internal fun modelName(info: IrFieldInfo, typeSet: TypeSet, rawTypename: String, isOther: Boolean): String {
  val responseName = if (info.type.isList()) {
    info.responseName.singularize()
  } else {
    info.responseName
  }
  val name = upperCamelCaseIgnoringNonLetters((typeSet - rawTypename).sorted() + responseName)

  return (if (isOther) "Other" else "") + name
}

/**
 * Build a simple model name. See also [modelName] for a more complex version that can use typeSets and fallback types
 */
internal fun modelName(info: IrFieldInfo): String {
  val responseName = if (info.type.isList()) {
    info.responseName.singularize()
  } else {
    info.responseName
  }
  return upperCamelCaseIgnoringNonLetters(setOf(responseName))
}


internal fun SchemaLayout.typePackageName() = "${schemaPackageName()}.type"
internal fun SchemaLayout.typeBuilderPackageName() = "${schemaPackageName()}.type.builder"
internal fun SchemaLayout.typeAdapterPackageName() = "${schemaPackageName()}.type.adapter"
internal fun SchemaLayout.typeUtilPackageName() = "${schemaPackageName()}.type.util"

internal fun SchemaLayout.paginationPackageName() = "${schemaPackageName()}.pagination"
internal fun SchemaLayout.schemaSubPackageName() = "${schemaPackageName()}.schema"
internal fun SchemaAndOperationsLayoutImpl.executionPackageName() = "${schemaPackageName()}.execution"

internal fun OperationsLayout.operationAdapterPackageName(filePath: String) = "${executableDocumentPackageName(filePath)}.adapter"
internal fun OperationsLayout.operationResponseFieldsPackageName(filePath: String) = "${executableDocumentPackageName(filePath)}.selections"

internal fun OperationsLayout.fragmentPackageName(filePath: String) = "${executableDocumentPackageName(filePath)}.fragment"
internal fun OperationsLayout.fragmentAdapterPackageName(filePath: String) = "${executableDocumentPackageName(filePath)}.fragment.adapter"
internal fun OperationsLayout.fragmentResponseFieldsPackageName(filePath: String) = "${executableDocumentPackageName(filePath)}.fragment.selections"

internal fun OperationsLayout.operationName(operation: IrOperation) = operationName(operation.name, operation.operationType.name)

internal fun String.responseAdapter(): String = "${this}_ResponseAdapter"
internal fun String.inputAdapter(): String = "${this}_InputAdapter"
internal fun String.variablesAdapter(): String = "${this}_VariablesAdapter"
internal fun String.impl(): String = "${this}Impl"
internal fun String.selections(): String = "${this}Selections"
/**
 * when used in function bodies, prefixing with '_' prevents clashing with parent classes
 */
internal fun String.variableName(): String = this.withUnderscorePrefix()

internal fun SchemaAndOperationsLayout(codegenSchema: CodegenSchema, packageNameGenerator: PackageNameGenerator, useSemanticNaming: Boolean, decapitalizeFields: Boolean): SchemaAndOperationsLayout = SchemaAndOperationsLayoutImpl(
    codegenSchema = codegenSchema,
    packageNameGenerator = packageNameGenerator,
    useSemanticNaming = useSemanticNaming,
    decapitalizeFields = decapitalizeFields,
)