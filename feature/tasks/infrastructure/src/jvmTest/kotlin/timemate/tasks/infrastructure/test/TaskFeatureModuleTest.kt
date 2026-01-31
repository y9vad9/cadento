package timemate.tasks.infrastructure.test

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import timemate.client.tasks.application.TimeZoneProvider
import timemate.tasks.infrastructure.TaskFeatureModule
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
