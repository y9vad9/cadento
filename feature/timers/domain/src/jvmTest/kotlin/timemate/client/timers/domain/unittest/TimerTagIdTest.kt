package timemate.client.timers.domain.unittest

import timemate.client.timers.domain.TimerTagId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TimerTagIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = TimerTagId.create(id)

        // THEN
        assertIs<TimerTagId.CreationResult.Success>(result)
        assertEquals(id, result.timerTagId.long)
    }

    @Test
    fun `create returns Success for zero ID`() {
        // GIVEN
        val id = 0L

        // WHEN
        val result = TimerTagId.create(id)

        // THEN
        assertIs<TimerTagId.CreationResult.Success>(result)
        assertEquals(id, result.timerTagId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val id = -1L

        // WHEN
        val result = TimerTagId.create(id)

        // THEN
        assertIs<TimerTagId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns TimerTagId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val timerTagId = TimerTagId.createOrThrow(id)

        // THEN
        assertEquals(id, timerTagId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val id = -5L

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TimerTagId.createOrThrow(id)
        }
    }

    @Test
    fun `create returns Success when value is equal to MIN_VALUE`() {
        // GIVEN
        val value = TimerTagId.MIN_VALUE

        // WHEN
        val result = TimerTagId.create(value)

        // THEN
        assertIs<TimerTagId.CreationResult.Success>(result)
        assertEquals(value, result.timerTagId.long)
    }

    @Test
    fun `create returns Negative for value less than MIN_VALUE`() {
        // GIVEN
        val invalidValue = TimerTagId.MIN_VALUE - 1

        // WHEN
        val result = TimerTagId.create(invalidValue)

        // THEN
        assertIs<TimerTagId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for value less than MIN_VALUE`() {
        // GIVEN
        val invalidValue = TimerTagId.MIN_VALUE - 1

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TimerTagId.createOrThrow(invalidValue)
        }
    }

    @Test
    fun `long property should return value passed to constructor`() {
        // GIVEN
        val idValue = 987L
        val timerTagId = TimerTagId.createOrThrow(idValue)

        // WHEN
        val result = timerTagId.long

        // THEN
        assertEquals(idValue, result)
    }
}
