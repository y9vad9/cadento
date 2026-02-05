package cadento.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("cadento.gradle.linter-convention")
    id("cadento.gradle.multiplatform-convention")
    id("cadento.gradle.tests-convention")
}

val libs = the<LibrariesForLibs>()
