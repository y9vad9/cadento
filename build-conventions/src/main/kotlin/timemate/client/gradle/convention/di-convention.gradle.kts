package timemate.client.gradle.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("app.cash.sqldelight")
    id("timemate.client.gradle.convention.detekt-convention")
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("com.google.devtools.ksp")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.koin.annotations)
    "kspCommonMainMetadata"(libs.koin.ksp.compiler)
}

ksp {
    allWarningsAsErrors = false
    arg("KOIN_DEFAULT_MODULE", "true")
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
