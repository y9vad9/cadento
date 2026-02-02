package timemate.client.gradle.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotlin.test)
    "jvmTestImplementation"(libs.mockk)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Test>("jvmUnitTest") {
    description = "Runs JVM unit tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    val jvmTest = tasks.named<Test>("jvmTest").get()
    testClassesDirs = jvmTest.testClassesDirs
    classpath = jvmTest.classpath

    filter {
        includeTestsMatching("timemate.**.unittest.**")
        isFailOnNoMatchingTests = false
    }
}
