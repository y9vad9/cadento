package cadento.tasks.infrastructure.unittest

import app.cash.sqldelight.db.SqlDriver
import cadento.tasks.application.TimeZoneProvider
import cadento.tasks.infrastructure.TaskFeatureModule
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import kotlin.test.Test
import kotlin.time.Clock

@OptIn(KoinExperimentalAPI::class)
class TaskFeatureModuleTest : KoinTest {

    @Test
    fun `verify task feature module`() {
        // GIVEN the TaskFeatureModule
        val module = TaskFeatureModule().module

        // THEN it should be valid with provided external dependencies
        module.verify(
            extraTypes = listOf(
                SqlDriver::class,
                Clock::class,
                TimeZoneProvider::class,
            ),
        )
    }
}
