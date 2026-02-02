package timemate.client.timers.domain.unittest

import timemate.client.timers.domain.PomodoroLongBreakPerShortBreaksCount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class PomodoroLongBreakPerShortBreaksCountTest {

    @Test
    fun `create returns Success for valid count`() {
        // GIVEN
        val count = 4

        // WHEN
        val result = PomodoroLongBreakPerShortBreaksCount.create(count)

        // THEN
        assertIs<PomodoroLongBreakPerShortBreaksCount.CreationResult.Success>(result)
        assertEquals(count, result.count.int)
    }

    @Test
    fun `create returns Success for min count`() {
        // GIVEN
        val count = 2

        // WHEN
        val result = PomodoroLongBreakPerShortBreaksCount.create(count)

        // THEN
        assertIs<PomodoroLongBreakPerShortBreaksCount.CreationResult.Success>(result)
        assertEquals(count, result.count.int)
    }

    @Test
    fun `create returns TooSmall for count below min`() {
        // GIVEN
        val count = 1

        // WHEN
        val result = PomodoroLongBreakPerShortBreaksCount.create(count)

        // THEN
        assertIs<PomodoroLongBreakPerShortBreaksCount.CreationResult.TooSmall>(result)
    }

    @Test
    fun `createOrThrow returns PomodoroLongBreakPerShortBreaksCount for valid count`() {
        // GIVEN
        val count = 5

        // WHEN
        val result = PomodoroLongBreakPerShortBreaksCount.createOrThrow(count)

        // THEN
        assertEquals(count, result.int)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid count`() {
        // GIVEN
        val count = 0

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            PomodoroLongBreakPerShortBreaksCount.createOrThrow(count)
        }
    }

    @Test
    fun `int property should return value passed to constructor`() {
        // GIVEN
        val count = 4
        val longBreakCount = PomodoroLongBreakPerShortBreaksCount.createOrThrow(count)

        // WHEN
        val result = longBreakCount.int

        // THEN
        assertEquals(count, result)
    }
}
