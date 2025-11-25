plugins {
    alias(libs.plugins.kotlin.android) // Using kotlin-android to access kotlin standard libs, but actually just kotlin-jvm is enough
    alias(libs.plugins.ksp)
}

// Since this is a pure Kotlin module for compiler, better to use kotlin("jvm") but we are in Android project context.
// Let's reset to pure kotlin jvm
apply(plugin = "org.jetbrains.kotlin.jvm")

dependencies {
    implementation(project(":my-hilt-annotations")) // To access annotations
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service)
}
