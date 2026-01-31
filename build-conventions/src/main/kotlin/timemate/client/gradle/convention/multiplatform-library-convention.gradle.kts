package timemate.client.gradle.convention

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("timemate.client.gradle.convention.kover-convention")
    id("timemate.client.gradle.convention.tests-convention")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
