@file:Suppress("unused")

/**
 * Defines access to all available convention plugin IDs used in this Gradle project.
 *
 * These constants are manually updated and help avoid typos or duplication when referencing
 * convention plugins from the included build `build-conventions`.
 *
 * Example usage:
 * ```kotlin
 * plugins {
 *     id(conventions.jvm)
 *     id(conventions.feature.di)
 *     id(conventions.feature.database)
 * }
 * ```
 */
val conventions: ConventionNamespace = ConventionNamespace()

/**
 * Top-level namespace that organizes convention plugins into meaningful groups
 * (e.g., `feature`, `jvm`, `multiplatform`, etc.).
 */
class ConventionNamespace internal constructor(
    /** ID of the base JVM convention plugin. */
    val jvm: String = "timemate.client.gradle.convention.jvm-convention",

    /** ID of the shared test conventions plugin. */
    val tests: String = "timemate.client.gradle.convention.tests-convention",

    /** Convention plugins used for feature modules (e.g., DI, Compose, Domain). */
    val feature: FeatureNamespace = FeatureNamespace(),

    /**
     * Convention plugin to enable kover in the module with default settings.
     */
    val kover: String = "timemate.client.gradle.convention.kover-convention",

    /** Convention plugins used in Kotlin Multiplatform projects. */
    val multiplatform: MultiplatformNamespace = MultiplatformNamespace()
)

/**
 * Convention plugins related to individual feature modules.
 */
class FeatureNamespace internal constructor(
    /** Convention plugin for DI setup in feature modules. */
    val di: String = "timemate.client.gradle.convention.feature.di-convention",

    /** Convention plugin for domain-layer setup in feature modules. */
    val domain: String = "timemate.client.gradle.convention.feature.domain-convention",

    /** Convention plugins related to integration-layer concerns like database, network, etc. */
    val integration: String = "timemate.client.gradle.convention.feature.integration-convention",

    val database: String = "timemate.client.gradle.convention.feature.integration-database-convention",

    val presentation: String = "timemate.client.gradle.convention.feature.presentation-convention",

    /** Convention plugins related to Compose UI setup in feature modules. */
    val compose: FeatureComposeNamespace = FeatureComposeNamespace(),
)

/**
 * Convention plugins related to Jetpack Compose UI within feature modules.
 */
class FeatureComposeNamespace internal constructor(
    /** Convention plugin for Compose UI setup. */
    val ui: String = "timemate.client.gradle.convention.feature.compose-ui-convention"
)


/**
 * Convention plugins used in Kotlin Multiplatform projects.
 */
class MultiplatformNamespace internal constructor(
    val core: String = "timemate.client.gradle.convention.multiplatform-convention",
    /** Convention plugin for setting up shared multiplatform libraries. */
    val library: String = "timemate.client.gradle.convention.multiplatform-library-convention"
)
