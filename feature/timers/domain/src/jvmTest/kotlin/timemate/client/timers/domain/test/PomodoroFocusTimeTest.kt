package timemate.client.timers.domain.test

import timemate.client.timers.domain.PomodoroFocusTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroFocusTimeTest {

    @Test
    fun `create returns Success for valid duration`() {
        // GIVEN
        val duration = 25.minutes

        // WHEN
        val result = PomodoroFocusTime.create(duration)

        // THEN
        assertIs<PomodoroFocusTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for min duration`() {
        // GIVEN
        val duration = 10.minutes

        // WHEN
        val result = PomodoroFocusTime.create(duration)

        // THEN
        assertIs<PomodoroFocusTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns Success for max duration`() {
        // GIVEN
        val duration = 1.hours

        // WHEN
        val result = PomodoroFocusTime.create(duration)

        // THEN
        assertIs<PomodoroFocusTime.CreationResult.Success>(result)
        assertEquals(duration, result.time.duration)
    }

    @Test
    fun `create returns TooShort for duration below min`() {
        // GIVEN
        val duration = 9.minutes + 59.seconds

        // WHEN
        val result = PomodoroFocusTime.create(duration)

        // THEN
        assertIs<PomodoroFocusTime.CreationResult.TooShort>(result)
    }

    @Test
    fun `create returns TooLong for duration above max`() {
        // GIVEN
        val duration = 1.hours + 1.seconds

        // WHEN
        val result = PomodoroFocusTime.create(duration)

        // THEN
        assertIs<PomodoroFocusTime.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroFocusTime for valid duration`() {
        // GIVEN
        val duration = 30.minutes

        // WHEN
        val time = PomodoroFocusTime.createOrThrow(duration)

        // THEN
        assertEquals(duration, time.duration)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid duration`() {
        // GIVEN
        val duration = 5.minutes

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroFocusTime.createOrThrow(duration)
        }
    }
}
