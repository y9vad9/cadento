package cadento.gradle

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("cadento.gradle.multiplatform-convention")
    id("cadento.gradle.kover-convention")
    id("cadento.gradle.tests-convention")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
