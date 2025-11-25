plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.12.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}

gradlePlugin {
    plugins {
        create("myHiltPlugin") {
            id = "com.github.georgeh1998.myhilt.plugin"
            implementationClass = "com.github.georgeh1998.myhilt.plugin.MyHiltPlugin"
        }
    }
}
