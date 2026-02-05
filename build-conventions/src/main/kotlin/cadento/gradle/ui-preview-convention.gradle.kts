package cadento.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("cadento.gradle.jvm-convention")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.compose.preview)
}