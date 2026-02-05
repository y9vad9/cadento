package cadento.gradle

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("cadento.gradle.linter-convention")
    id("cadento.gradle.multiplatform-convention")
    id("cadento.gradle.tests-convention")
    id("cadento.gradle.kover-convention")
}

kover {
    reports {
        verify.rule {
            /**
             * We want to enforce application to be fully tested as logic there
             * is totally deterministic, pure; therefore, should be well-tested
             */
            minBound(
                minValue = 90,
                coverageUnits = CoverageUnit.LINE,
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
            )
        }
    }
}
