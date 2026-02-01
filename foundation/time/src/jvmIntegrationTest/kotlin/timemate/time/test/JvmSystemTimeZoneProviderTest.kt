package timemate.time.test

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import timemate.time.JvmSystemTimeZoneProvider
import java.util.TimeZone as JTimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class JvmSystemTimeZoneProviderTest {

    @BeforeTest
    fun setup() {
        mockkStatic(JTimeZone::class)
    }

    @AfterTest
    fun tearDown() {
        unmockkStatic(JTimeZone::class)
    }

    @Test
    fun `timeZone returns current system timezone initially`() = runTest {
        // GIVEN a mocked system timezone
        val expectedTz = JTimeZone.getTimeZone("UTC")
        every { JTimeZone.setDefault(null) } returns Unit
        every { JTimeZone.getDefault() } returns expectedTz

        // WHEN we create a provider
        val provider = JvmSystemTimeZoneProvider(
            scope = this,
        )

        // THEN it returns the mocked timezone
        assertEquals(
            expected = expectedTz.id,
            actual = provider.timeZone.value.id,
            message = "Initial timezone should match mocked system default",
        )
        
        coroutineContext[Job]?.cancelChildren()
    }

    @Test
    fun `timeZone emits updated timezone after poll interval`() = runTest {
        // GIVEN a provider with short poll interval and mocked timezones
        val pollInterval = 1.seconds
        val initialTz = JTimeZone.getTimeZone("UTC")
        val updatedTz = JTimeZone.getTimeZone("GMT+01:00")
        
        every { JTimeZone.setDefault(null) } returns Unit
        every { JTimeZone.getDefault() } returns initialTz
        
        val provider = JvmSystemTimeZoneProvider(
            scope = this,
            pollInterval = pollInterval,
        )

        provider.timeZone.test {
            val initial = awaitItem()
            assertEquals(
                expected = initialTz.id,
                actual = initial.id,
                message = "First emitted item should be the initial system timezone",
            )

            // WHEN the system default changes (mocked)
            every { JTimeZone.getDefault() } returns updatedTz

            // AND we advance time past poll interval
            advanceTimeBy(pollInterval)
            runCurrent()

            // THEN the flow should emit the new timezone
            val updated = awaitItem()
            assertEquals(
                expected = updatedTz.id,
                actual = updated.id,
                message = "Updated item should match the new system default after polling",
            )
            
            cancelAndIgnoreRemainingEvents()
        }
        
        coroutineContext[Job]?.cancelChildren()
    }
}