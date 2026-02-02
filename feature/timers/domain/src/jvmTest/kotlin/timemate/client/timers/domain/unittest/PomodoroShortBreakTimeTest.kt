package timemate.client.timers.domain.unittest

import timemate.client.timers.domain.PomodoroShortBreakTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroShortBreakTimeTest {

    @Test
    fun `create returns Success for valid duration`() {
        // GIVEN
        val duration = 5.minutes

        // WHEN
        val result = PomodoroShortBreakTime.create(duration)

        // THEN
        assertIs<PomodoroShortBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for min duration`() {
        // GIVEN
        val duration = 1.minutes

        // WHEN
        val result = PomodoroShortBreakTime.create(duration)

        // THEN
        assertIs<PomodoroShortBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for max duration`() {
        // GIVEN
        val duration = 20.minutes

        // WHEN
        val result = PomodoroShortBreakTime.create(duration)

        // THEN
        assertIs<PomodoroShortBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns TooShort for duration below min`() {
        // GIVEN
        val duration = 59.seconds

        // WHEN
        val result = PomodoroShortBreakTime.create(duration)

        // THEN
        assertIs<PomodoroShortBreakTime.CreationResult.TooShort>(result)
    }

    @Test
    fun `create returns TooLong for duration above max`() {
        // GIVEN
        val duration = 20.minutes + 1.seconds

        // WHEN
        val result = PomodoroShortBreakTime.create(duration)

        // THEN
        assertIs<PomodoroShortBreakTime.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroShortBreakTime for valid duration`() {
        // GIVEN
        val duration = 10.minutes

        // WHEN
        val time = PomodoroShortBreakTime.createOrThrow(duration)

        // THEN
        assertEquals(duration, time.duration)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid duration`() {
        // GIVEN
        val duration = 30.minutes

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroShortBreakTime.createOrThrow(duration)
        }
    }

    @Test
    fun `duration property should return value passed to constructor`() {
        // GIVEN
        val duration = 5.minutes
        val shortBreakTime = PomodoroShortBreakTime.createOrThrow(duration)

        // WHEN
        val result = shortBreakTime.duration

        // THEN
        assertEquals(duration, result)
    }

    @Test
    fun `TIME_RANGE property should return correct time range`() {
        // WHEN
        val timeRange = PomodoroShortBreakTime.TIME_RANGE

        // THEN
        assertEquals(1.minutes..20.minutes, timeRange)
    }
}
