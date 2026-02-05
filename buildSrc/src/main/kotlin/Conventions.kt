@file:Suppress("unused")

/**
 * Defines access to all available high-level convention plugin IDs used in this Gradle project.
 *
 * These constants are manually updated and help avoid typos or duplication when referencing
 * convention plugins from the included build `build-conventions`.
 */
val conventions: ConventionNamespace = ConventionNamespace()

/**
 * Top-level namespace that organizes high-level convention plugins.
 */
class ConventionNamespace internal constructor(
    /** Convention plugin for domain-layer setup. */
    val domain: String = "cadento.gradle.domain-convention",

    /** Convention plugin for application-layer setup. */
    val application: String = "cadento.gradle.application-convention",

    /** Convention plugin for UI preview modules. */
    val uiPreview: String = "cadento.gradle.ui-preview-convention",

    /** Kotlin Multiplatform related high-level conventions. */
    val multiplatform: MultiplatformNamespace = MultiplatformNamespace(),

    /** Feature-specific high-level conventions. */
    val feature: FeatureNamespace = FeatureNamespace(),
)

/**
 * Convention plugins used in Kotlin Multiplatform projects.
 */
class MultiplatformNamespace internal constructor(
    /** Convention plugin for setting up shared multiplatform libraries. */
    val library: String = "cadento.gradle.multiplatform-library-convention"
)

/**
 * Convention plugins related to individual feature modules integration and presentation.
 */
class FeatureNamespace internal constructor(
    /** Convention plugins related to integration-layer concerns like database, network, etc. */
    val infrastructure: String = "cadento.gradle.infrastructure-convention",

    /** Convention plugin for database setup. */
    val database: String = "cadento.gradle.database-convention",

    /** Convention plugin for presentation-layer setup. */
    val presentation: String = "cadento.gradle.presentation-convention",

    /** Convention plugins related to Compose UI setup in feature modules. */
    val compose: FeatureComposeNamespace = FeatureComposeNamespace(),
)

/**
 * Convention plugins related to Jetpack Compose UI within feature modules.
 */
class FeatureComposeNamespace internal constructor(
    /** Convention plugin for Compose UI setup. */
    val ui: String = "cadento.gradle.compose-ui-convention"
)