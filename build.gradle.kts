// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android and Kotlin plugins are added to classpath by buildSrc, so we avoid version conflict by not defining them here with alias.
    // Instead we rely on subprojects applying them, or define them without version if needed.
    // alias(libs.plugins.android.application) apply false
    // alias(libs.plugins.android.library) apply false
    // alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
