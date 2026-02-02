package timemate.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.gradle.multiplatform-convention")
    id("timemate.gradle.linter-convention")
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