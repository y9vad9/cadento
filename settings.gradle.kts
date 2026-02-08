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

rootProject.name = "cadento"

includeBuild("build-conventions")

include(
    ":feature:core:domain",
)

include(
    ":feature:tasks:domain",
    ":feature:tasks:application",
    ":feature:tasks:infrastructure",
    ":feature:tasks:infrastructure:database",
    ":feature:tasks:presentation",
    ":feature:tasks:presentation:compose-ui",
)

include(
    ":feature:timers:domain",
    ":feature:timers:application",
    ":feature:timers:infrastructure",
    ":feature:timers:infrastructure:database",
    ":feature:timers:presentation",
    ":feature:timers:presentation:compose-ui",
)

include(
    ":foundation:time",
    ":foundation:coroutines",
)

include(
    ":ui-kit",
    ":ui-kit:material3",
    ":ui-kit:unstyled-desktop",
    ":ui-kit:preview",
)
