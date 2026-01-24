package timemate.client.gradle.convention

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val libs = the<LibrariesForLibs>()

detekt {
    toolVersion = libs.versions.detekt.asProvider().get()

    source.from(
        /**
         * ┌────────────────────┐
         * │ Root Sources       │
         * └────────────────────┘
         */
        files("src/main/kotlin"),
        files("src/test/kotlin"),

        /**
         * ┌────────────────────┐
         * │ Common (MPP)       │
         * └────────────────────┘
         */
        files("src/commonMain/kotlin"),
        files("src/commonTest/kotlin"),
        files("src/commonTestFixtures/kotlin"),

        /**
         * ┌────────────────────┐
         * │ JVM Targets        │
         * └────────────────────┘
         */
        files("src/jvmMain/kotlin"),
        files("src/jvmTest/kotlin"),

        /**
         * ┌────────────────────┐
         * │ Android Targets    │
         * └────────────────────┘
         */
        files("src/androidMain/kotlin"),
        files("src/androidTest/kotlin"),

        /**
         * ┌────────────────────┐
         * │ iOS Targets        │
         * └────────────────────┘
         */
        files("src/iosMain/kotlin"),
        files("src/iosTest/kotlin"),
        files("src/iosX64Main/kotlin"),
        files("src/iosX64Test/kotlin"),
        files("src/iosArm64Main/kotlin"),
        files("src/iosArm64Test/kotlin"),
        files("src/iosSimulatorArm64Main/kotlin"),
        files("src/iosSimulatorArm64Test/kotlin"),

        /**
         * ┌────────────────────┐
         * │ Desktop Targets    │
         * └────────────────────┘
         */
        files("src/desktopMain/kotlin"),
        files("src/desktopTest/kotlin"),

        /**
         * ┌────────────────────┐
         * │ Test Fixtures      │
         * └────────────────────┘
         */
        files("src/testFixtures/kotlin")
    )

    parallel = true

    config.from(rootProject.file("detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}