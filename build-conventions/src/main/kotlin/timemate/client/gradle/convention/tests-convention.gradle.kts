package timemate.client.gradle.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotlin.test)
    "jvmMainImplementation"(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}