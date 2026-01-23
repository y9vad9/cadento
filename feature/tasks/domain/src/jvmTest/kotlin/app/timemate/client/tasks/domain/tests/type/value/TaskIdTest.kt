package app.timemate.client.tasks.domain.tests.type.value

import app.timemate.client.tasks.domain.type.value.TaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TaskIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = TaskId.create(id)

        // THEN
        assertIs<TaskId.CreationResult.Success>(result)
        assertEquals(id, result.taskId.long)
    }

    @Test
    fun `create returns Success for zero ID`() {
        // GIVEN
        val id = 0L

        // WHEN
        val result = TaskId.create(id)

        // THEN
        assertIs<TaskId.CreationResult.Success>(result)
        assertEquals(id, result.taskId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val id = -1L

        // WHEN
        val result = TaskId.create(id)

        // THEN
        assertIs<TaskId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns TaskId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val taskId = TaskId.createOrThrow(id)

        // THEN
        assertEquals(id, taskId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val id = -5L

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskId.createOrThrow(id)
        }
    }
}
