package app.timemate.client.timers.domain.test.type.task.value

import app.timemate.client.timers.domain.type.task.value.LinkedTaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class LinkedTaskIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = LinkedTaskId.create(id)

        // THEN
        assertIs<LinkedTaskId.CreationResult.Success>(result)
        assertEquals(id, result.linkedTaskId.long)
    }

    @Test
    fun `create returns Success for zero ID`() {
        // GIVEN
        val id = 0L

        // WHEN
        val result = LinkedTaskId.create(id)

        // THEN
        assertIs<LinkedTaskId.CreationResult.Success>(result)
        assertEquals(id, result.linkedTaskId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val id = -1L

        // WHEN
        val result = LinkedTaskId.create(id)

        // THEN
        assertIs<LinkedTaskId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns LinkedTaskId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val linkedTaskId = LinkedTaskId.createOrThrow(id)

        // THEN
        assertEquals(id, linkedTaskId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val id = -5L

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            LinkedTaskId.createOrThrow(id)
        }
    }
}
