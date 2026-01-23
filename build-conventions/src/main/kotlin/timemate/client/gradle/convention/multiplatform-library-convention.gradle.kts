package timemate.client.gradle.convention

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("timemate.client.gradle.convention.multiplatform-convention")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}