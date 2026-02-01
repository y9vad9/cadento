package timemate.client.gradle.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    id("timemate.client.gradle.convention.tests-convention")
}

val libs = the<LibrariesForLibs>()

kotlin {
    val jvmCompilations = targets.getByName("jvm").compilations

    targets.named<KotlinJvmTarget>("jvm") {
        compilations.create("integrationTest") {
            associateWith(jvmCompilations["test"])
            defaultSourceSet {
                kotlin.srcDir("src/jvmIntegrationTest/kotlin")
                resources.srcDir("src/jvmIntegrationTest/resources")

                dependencies {
                    implementation(libs.kotlin.test)
                    implementation(libs.kotlin.test.junit5)
                }
            }
        }
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs JVM integration tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    useJUnitPlatform()

    val compilation =
        kotlin.targets["jvm"]
            .compilations["integrationTest"]

    testClassesDirs = compilation.output.classesDirs
    classpath = files(
        compilation.output.allOutputs,
        compilation.runtimeDependencyFiles
    )

    shouldRunAfter(tasks.named("jvmTest"))
}
