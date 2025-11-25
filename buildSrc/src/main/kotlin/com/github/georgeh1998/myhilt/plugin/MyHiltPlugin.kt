package com.github.georgeh1998.myhilt.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MyHiltPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                HiltClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}
            // ASM code runs on frames which is important for class hierarchy changes
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
    }
}

abstract class HiltClassVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return HiltClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        // Only instrument classes annotated with @HiltAndroidApp
        return classData.classAnnotations.contains("com.github.georgeh1998.myhilt.annotations.HiltAndroidApp")
    }
}

class HiltClassVisitor(nextVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM9, nextVisitor) {
    private var oldSuperName: String? = null
    private var newSuperName: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        // Change the superclass to Hilt_<ClassName>
        val lastSlash = name.lastIndexOf('/')
        val packageName = if (lastSlash >= 0) name.substring(0, lastSlash + 1) else ""
        val simpleName = if (lastSlash >= 0) name.substring(lastSlash + 1) else name

        this.oldSuperName = superName
        this.newSuperName = "${packageName}Hilt_$simpleName"

        super.visit(version, access, name, signature, this.newSuperName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return object : MethodVisitor(Opcodes.ASM9, mv) {
            override fun visitMethodInsn(
                opcode: Int,
                owner: String?,
                name: String?,
                descriptor: String?,
                isInterface: Boolean
            ) {
                // If the method calls super.<init> or super.onCreate using the OLD superclass,
                // redirect it to the NEW superclass.
                if (opcode == Opcodes.INVOKESPECIAL && owner == oldSuperName && newSuperName != null) {
                    super.visitMethodInsn(opcode, newSuperName, name, descriptor, isInterface)
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }
    }
}
