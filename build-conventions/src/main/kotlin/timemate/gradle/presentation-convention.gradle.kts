package timemate.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.gradle.linter-convention")
    id("timemate.gradle.multiplatform-convention")
    id("timemate.gradle.tests-convention")
}

val libs = the<LibrariesForLibs>()
