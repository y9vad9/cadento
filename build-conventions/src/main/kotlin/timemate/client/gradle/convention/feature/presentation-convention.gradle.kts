package timemate.client.gradle.convention.feature

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.client.gradle.convention.detekt-convention")
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("timemate.client.gradle.convention.tests-convention")
}

val libs = the<LibrariesForLibs>()
