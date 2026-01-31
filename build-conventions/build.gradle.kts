plugins {
    `kotlin-dsl`
}

group = "build-conventions"
version = "SNAPSHOT"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.pluginClasspath.kotlin)
    api(libs.pluginClasspath.sqldelight)
    api(libs.pluginClasspath.compose)
    api(libs.pluginClasspath.compose.compiler)
    api(libs.pluginClasspath.kover)
    api(libs.pluginClasspath.ksp)
    api(libs.pluginClasspath.detekt)
    api(files((libs).javaClass.superclass.protectionDomain.codeSource.location))
}