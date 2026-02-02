package timemate.client.timers.domain.unittest

import timemate.client.timers.domain.PomodoroLongBreakTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroLongBreakTimeTest {

    @Test
    fun `create returns Success for valid duration`() {
        // GIVEN
        val duration = 15.minutes

        // WHEN
        val result = PomodoroLongBreakTime.create(duration)

        // THEN
        assertIs<PomodoroLongBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for min duration`() {
        // GIVEN
        val duration = 3.minutes

        // WHEN
        val result = PomodoroLongBreakTime.create(duration)

        // THEN
        assertIs<PomodoroLongBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for max duration`() {
        // GIVEN
        val duration = 1.hours

        // WHEN
        val result = PomodoroLongBreakTime.create(duration)

        // THEN
        assertIs<PomodoroLongBreakTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns TooShort for duration below min`() {
        // GIVEN
        val duration = 2.minutes + 59.seconds

        // WHEN
        val result = PomodoroLongBreakTime.create(duration)

        // THEN
        assertIs<PomodoroLongBreakTime.CreationResult.TooShort>(result)
    }

    @Test
    fun `create returns TooLong for duration above max`() {
        // GIVEN
        val duration = 1.hours + 1.seconds

        // WHEN
        val result = PomodoroLongBreakTime.create(duration)

        // THEN
        assertIs<PomodoroLongBreakTime.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroLongBreakTime for valid duration`() {
        // GIVEN
        val duration = 20.minutes

        // WHEN
        val time = PomodoroLongBreakTime.createOrThrow(duration)

        // THEN
        assertEquals(duration, time.duration)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid duration`() {
        // GIVEN
        val duration = 1.minutes

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroLongBreakTime.createOrThrow(duration)
        }
    }

    @Test
    fun `duration property should return value passed to constructor`() {
        // GIVEN
        val duration = 15.minutes
        val longBreakTime = PomodoroLongBreakTime.createOrThrow(duration)

        // WHEN
        val result = longBreakTime.duration

        // THEN
        assertEquals(duration, result)
    }

    @Test
    fun `TIME_RANGE property should return correct time range`() {
        // WHEN
        val timeRange = PomodoroLongBreakTime.TIME_RANGE

        // THEN
        assertEquals(3.minutes..1.hours, timeRange)
    }
}
