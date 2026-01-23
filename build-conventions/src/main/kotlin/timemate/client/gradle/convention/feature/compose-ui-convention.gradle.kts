package timemate.client.gradle.convention.feature

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("timemate.client.gradle.convention.detekt-convention")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

dependencies {
    // Compose
    commonMainImplementation(compose.ui)
    @OptIn(ExperimentalComposeLibrary::class)
    commonTestImplementation(compose.uiTest)
    commonMainImplementation(compose.material3)

    // Detekt plugins
    detektPlugins(libs.detekt.compose)
}