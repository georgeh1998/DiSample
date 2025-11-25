package com.github.georgeh1998.myhilt.compiler

import com.github.georgeh1998.myhilt.annotations.HiltAndroidApp
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

@AutoService(SymbolProcessorProvider::class)
class HiltProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HiltProcessor(environment.codeGenerator, environment.logger)
    }
}

class HiltProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(HiltAndroidApp::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }.toList()

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(HiltVisitor(), Unit) }

        return ret
    }

    inner class HiltVisitor : com.google.devtools.ksp.symbol.KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val packageName = classDeclaration.packageName.asString()
            val simpleName = classDeclaration.simpleName.asString()
            val hiltName = "Hilt_$simpleName"

            val initStatement = "com.github.georgeh1998.myhilt.internal.MyHilt.init(this)"

            val fileSpec = FileSpec.builder(packageName, hiltName)
                .addType(
                    TypeSpec.classBuilder(hiltName)
                        .addModifiers(KModifier.OPEN)
                        .superclass(ClassName("android.app", "Application")) // Assuming direct inheritance from Application for simplicity in sample
                        // In real Hilt, it detects the super type. For this sample, we assume : Application()
                        .addFunction(
                            FunSpec.builder("onCreate")
                                .addModifiers(KModifier.OVERRIDE)
                                .addStatement("super.onCreate()")
                                .addStatement(initStatement)
                                .build()
                        )
                        .build()
                )
                .build()

            fileSpec.writeTo(codeGenerator, false)
        }
    }
}
