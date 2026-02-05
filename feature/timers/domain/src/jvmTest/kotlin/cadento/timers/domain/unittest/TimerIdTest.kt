package cadento.timers.domain.unittest

import cadento.timers.domain.TimerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TimerIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = TimerId.create(id)

        // THEN
        assertIs<TimerId.CreationResult.Success>(result)
        assertEquals(id, result.timerId.long)
    }

    @Test
    fun `create returns Success for zero ID`() {
        // GIVEN
        val id = 0L

        // WHEN
        val result = TimerId.create(id)

        // THEN
        assertIs<TimerId.CreationResult.Success>(result)
        assertEquals(id, result.timerId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val id = -1L

        // WHEN
        val result = TimerId.create(id)

        // THEN
        assertIs<TimerId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns TimerId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val timerId = TimerId.createOrThrow(id)

        // THEN
        assertEquals(id, timerId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val id = -5L

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TimerId.createOrThrow(id)
        }
    }

    @Test
    fun `long property should return value passed to constructor`() {
        // GIVEN
        val idValue = 789L
        val timerId = TimerId.createOrThrow(idValue)

        // WHEN
        val result = timerId.long

        // THEN
        assertEquals(idValue, result)
    }
}
