package app.timemate.client.build.conventions.feature

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("app.timemate.client.build.conventions.detekt-convention")
    id("app.timemate.client.build.conventions.multiplatform-convention")
    id("app.timemate.client.build.conventions.tests-convention")
}

val libs = the<LibrariesForLibs>()
