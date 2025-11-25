plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("myHiltPlugin") {
            id = "com.github.georgeh1998.myhilt.plugin"
            implementationClass = "com.github.georgeh1998.myhilt.plugin.MyHiltPlugin"
        }
    }
}
