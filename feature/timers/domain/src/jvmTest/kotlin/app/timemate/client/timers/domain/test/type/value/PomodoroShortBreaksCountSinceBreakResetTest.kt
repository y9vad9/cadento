package app.timemate.client.timers.domain.test.type.value

import app.timemate.client.timers.domain.type.value.PomodoroShortBreaksCountSinceBreakReset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class PomodoroShortBreaksCountSinceBreakResetTest {

    @Test
    fun `create returns Success for valid positive count`() {
        // GIVEN
        val count = 3

        // WHEN
        val result = PomodoroShortBreaksCountSinceBreakReset.create(count)

        // THEN
        assertIs<PomodoroShortBreaksCountSinceBreakReset.CreationResult.Success>(result)
        assertEquals(count, result.count.int)
    }

    @Test
    fun `create returns Success for zero count`() {
        // GIVEN
        val count = 0

        // WHEN
        val result = PomodoroShortBreaksCountSinceBreakReset.create(count)

        // THEN
        assertIs<PomodoroShortBreaksCountSinceBreakReset.CreationResult.Success>(result)
        assertEquals(count, result.count.int)
    }

    @Test
    fun `create returns Negative for negative count`() {
        // GIVEN
        val count = -1

        // WHEN
        val result = PomodoroShortBreaksCountSinceBreakReset.create(count)

        // THEN
        assertIs<PomodoroShortBreaksCountSinceBreakReset.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroShortBreaksCountSinceBreakReset for valid count`() {
        // GIVEN
        val count = 5

        // WHEN
        val result = PomodoroShortBreaksCountSinceBreakReset.createOrThrow(count)

        // THEN
        assertEquals(count, result.int)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative count`() {
        // GIVEN
        val count = -1

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroShortBreaksCountSinceBreakReset.createOrThrow(count)
        }
    }
}
