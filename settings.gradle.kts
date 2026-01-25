enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
}

rootProject.name = "timemate-app"

includeBuild("build-conventions")

include(
    ":feature:core:domain",
)

include(
    ":feature:tasks:domain",
    ":feature:tasks:integration",
    ":feature:tasks:presentation",
    ":feature:tasks:presentation:compose-ui",
    ":feature:tasks:dependencies",
)

include(
    ":feature:timers:domain",
    ":feature:tasks:application",
    ":feature:timers:integration",
    ":feature:timers:presentation",
    ":feature:timers:presentation:compose-ui",
    ":feature:timers:dependencies",
)

include(
    ":ui-kit",
    ":ui-kit:material3",
    ":ui-kit:preview",
)