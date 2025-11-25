plugins {
    alias(libs.plugins.kotlin.android) apply false // Don't need android
    kotlin("jvm") version "2.0.21"
}

dependencies {
    implementation(libs.javax.inject) // If needed, or just pure kotlin
}
