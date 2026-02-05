package cadento.tasks.application.unittest

import app.cash.turbine.test
import cadento.tasks.application.localDateTimeFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class ClockExtensionsTest {

    private val clock: Clock = mockk()

    @Test
    fun `localDateTimeFlow emits new item on timezone change`() = runTest {
        // GIVEN
        val initialTime = LocalDateTime(2024, 1, 1, 10, 0).toInstant(TimeZone.UTC)
        every { clock.now() } returns initialTime

        val timeZoneFlow = MutableStateFlow<TimeZone>(TimeZone.UTC)
        val flow = clock.localDateTimeFlow(timeZoneFlow)

        // WHEN & THEN
        flow.test {
            // Initial emission
            var expected = initialTime.toLocalDateTime(TimeZone.UTC)
            assertEquals(expected, awaitItem())

            // Change timezone
            val newTimeZone = TimeZone.of("America/New_York")
            timeZoneFlow.value = newTimeZone

            // New emission
            expected = initialTime.toLocalDateTime(newTimeZone)
            assertEquals(expected, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `localDateTimeFlow emits new item after midnight`() = runTest {
        // GIVEN
        val startTime = LocalDateTime(2024, 1, 1, 23, 59).toInstant(TimeZone.UTC)
        val afterMidnight = startTime + 2.hours // Move time past midnight
        every { clock.now() } returns startTime andThen afterMidnight

        val timeZoneFlow = MutableStateFlow(TimeZone.UTC)
        val flow = clock.localDateTimeFlow(timeZoneFlow)

        // WHEN & THEN
        flow.test {
            // Initial emission before midnight
            assertEquals(startTime.toLocalDateTime(TimeZone.UTC), awaitItem())

            // Second emission after midnight
            assertEquals(afterMidnight.toLocalDateTime(TimeZone.UTC), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
