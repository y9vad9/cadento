package app.timemate.client.timers.domain.test.type.tag.value

import app.timemate.client.timers.domain.type.tag.value.TimerTagId
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
}
