package cadento.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("cadento.gradle.multiplatform-convention")
    id("cadento.gradle.linter-convention")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

dependencies {
    // Compose
    commonMainImplementation(libs.compose.ui)
    commonMainImplementation(libs.compose.material3)

    // Detekt plugins
    detektPlugins(libs.detekt.compose)
}