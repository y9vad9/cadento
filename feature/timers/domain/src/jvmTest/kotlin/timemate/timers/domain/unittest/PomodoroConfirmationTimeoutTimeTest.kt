package timemate.timers.domain.unittest

import timemate.timers.domain.PomodoroConfirmationTimeoutTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroConfirmationTimeoutTimeTest {

    @Test
    fun `create returns Success for valid duration`() {
        // GIVEN
        val duration = 30.seconds

        // WHEN
        val result = PomodoroConfirmationTimeoutTime.create(duration)

        // THEN
        assertIs<PomodoroConfirmationTimeoutTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for min duration`() {
        // GIVEN
        val duration = 5.seconds

        // WHEN
        val result = PomodoroConfirmationTimeoutTime.create(duration)

        // THEN
        assertIs<PomodoroConfirmationTimeoutTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for max duration`() {
        // GIVEN
        val duration = 5.minutes

        // WHEN
        val result = PomodoroConfirmationTimeoutTime.create(duration)

        // THEN
        assertIs<PomodoroConfirmationTimeoutTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns TooShort for duration below min`() {
        // GIVEN
        val duration = 4.seconds

        // WHEN
        val result = PomodoroConfirmationTimeoutTime.create(duration)

        // THEN
        assertIs<PomodoroConfirmationTimeoutTime.CreationResult.TooShort>(result)
    }

    @Test
    fun `create returns TooLong for duration above max`() {
        // GIVEN
        val duration = 5.minutes + 1.seconds

        // WHEN
        val result = PomodoroConfirmationTimeoutTime.create(duration)

        // THEN
        assertIs<PomodoroConfirmationTimeoutTime.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroConfirmationTimeoutTime for valid duration`() {
        // GIVEN
        val duration = 1.minutes

        // WHEN
        val time = PomodoroConfirmationTimeoutTime.createOrThrow(duration)

        // THEN
        assertEquals(duration, time.duration)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid duration`() {
        // GIVEN
        val duration = 10.minutes

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroConfirmationTimeoutTime.createOrThrow(duration)
        }
    }

    @Test
    fun `duration property should return value passed to constructor`() {
        // GIVEN
        val duration = 30.seconds
        val timeoutTime = PomodoroConfirmationTimeoutTime.createOrThrow(duration)

        // WHEN
        val result = timeoutTime.duration

        // THEN
        assertEquals(duration, result)
    }

    @Test
    fun `TIME_RANGE property should return correct time range`() {
        // WHEN
        val timeRange = PomodoroConfirmationTimeoutTime.TIME_RANGE

        // THEN
        assertEquals(5.seconds..5.minutes, timeRange)
    }
}
