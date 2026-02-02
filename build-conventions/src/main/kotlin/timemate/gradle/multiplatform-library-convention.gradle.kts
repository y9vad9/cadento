package timemate.gradle

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("timemate.gradle.multiplatform-convention")
    id("timemate.gradle.kover-convention")
    id("timemate.gradle.tests-convention")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
