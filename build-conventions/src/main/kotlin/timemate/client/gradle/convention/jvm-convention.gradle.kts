package timemate.client.gradle.convention

plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)

    sourceSets.all {
        compilerOptions {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")

            /**
             * We want to keep both code and build as clean as possible.
             * Warnings tend to grow technical debt; therefore, we avoid it by considering
             * warnings as errors
             */
            allWarningsAsErrors = true
        }
    }
}