package timemate.client.gradle.convention

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    // Dummy target to avoid `commonMain` access to jvm classes
    js {
        browser()
    }

    jvmToolchain(11)

    sourceSets {
        all {
            sourceSets {
                all {
                    languageSettings.optIn("kotlin.time.ExperimentalTime")
                    languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
                }
            }
        }
    }

    sourceSets {
        all {
            compilerOptions {
                /**
                 * We want to keep both code and build as clean as possible.
                 * Warnings tend to grow technical debt; therefore, we avoid it by considering
                 * warnings as errors
                 */
                allWarningsAsErrors = true
            }
        }
    }
}