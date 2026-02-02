package timemate.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.gradle.linter-convention")
    id("timemate.gradle.multiplatform-convention")
    id("timemate.gradle.tests-convention")
    id("app.cash.sqldelight")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonMainImplementation(libs.sqldelight.runtime)
}