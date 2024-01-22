package com.apollographql.apollo3.gradle.internal

import com.apollographql.apollo3.compiler.ApolloCompiler
import com.apollographql.apollo3.compiler.toCodegenSchema
import com.apollographql.apollo3.compiler.toIrOperations
import com.apollographql.apollo3.compiler.toIrOptions
import com.apollographql.apollo3.compiler.writeTo
import com.apollographql.apollo3.gradle.internal.ApolloGenerateSourcesFromIrTask.Companion.findCodegenSchemaFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class ApolloGenerateIrOperationsTask: DefaultTask() {
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val codegenSchemaFiles: ConfigurableFileCollection

  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val graphqlFiles: ConfigurableFileCollection

  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val upstreamIrFiles: ConfigurableFileCollection

  @get:InputFile
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val irOptionsFile: RegularFileProperty

  @get:OutputFile
  abstract val irOperationsFile: RegularFileProperty

  @TaskAction
  fun taskAction() {
    ApolloCompiler.buildIrOperations(
        executableFiles = graphqlFiles.files,
        codegenSchema = codegenSchemaFiles.files.findCodegenSchemaFile().toCodegenSchema(),
        upstreamIrOperations = upstreamIrFiles.files.map { it.toIrOperations() },
        irOptions = irOptionsFile.get().asFile.toIrOptions(),
        logger = logger(),
        operationOutputGenerator = compilerPlugin()?.operationOutputGenerator()
    ).writeTo(irOperationsFile.get().asFile)
  }
}