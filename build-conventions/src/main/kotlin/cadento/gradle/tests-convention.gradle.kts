package cadento.gradle

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("cadento.gradle.multiplatform-convention")
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
        includeTestsMatching("cadento.**.unittest.**")
        isFailOnNoMatchingTests = false
    }
}

tasks.register<Test>("jvmIntegrationTest") {
    description = "Runs JVM integration tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    val jvmTest = tasks.named<Test>("jvmTest").get()
    testClassesDirs = jvmTest.testClassesDirs
    classpath = jvmTest.classpath

    filter {
        includeTestsMatching("cadento.**.integrationtest.**")
        isFailOnNoMatchingTests = false
    }
}