package timemate.client.gradle.convention.feature

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("timemate.client.gradle.convention.detekt-convention")
    id("timemate.client.gradle.convention.multiplatform-convention")
    id("timemate.client.gradle.convention.tests-convention")
    id("timemate.client.gradle.convention.kover-convention")
}

kover {
    reports {
        verify.rule {
            /**
             * We want to enforce domain to be fully tested as logic there
             * is totally deterministic, pure; therefore, should be well-tested
             */
            minBound(
                minValue = 85,
                coverageUnits = CoverageUnit.LINE,
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
            )
        }
    }
}
