package timemate.client.gradle.convention.feature

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("timemate.client.gradle.convention.detekt-convention")
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