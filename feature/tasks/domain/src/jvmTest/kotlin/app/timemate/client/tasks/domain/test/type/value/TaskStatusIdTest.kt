package app.timemate.client.tasks.domain.test.type.value

import app.timemate.client.tasks.domain.type.value.TaskStatusId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TaskStatusIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = TaskStatusId.create(id)

        // THEN
        assertIs<TaskStatusId.CreationResult.Success>(result)
        assertEquals(id, result.taskStatusId.long)
    }

    @Test
    fun `create returns Success for min value ID`() {
        // GIVEN
        val id = TaskStatusId.MIN_VALUE

        // WHEN
        val result = TaskStatusId.create(id)

        // THEN
        assertIs<TaskStatusId.CreationResult.Success>(result)
        assertEquals(id, result.taskStatusId.long)
    }

    @Test
    fun `create returns TooSmall for ID below min value`() {
        // GIVEN
        val id = TaskStatusId.MIN_VALUE - 1

        // WHEN
        val result = TaskStatusId.create(id)

        // THEN
        assertIs<TaskStatusId.CreationResult.TooSmall>(result)
    }

    @Test
    fun `createOrThrow returns TaskStatusId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val taskStatusId = TaskStatusId.createOrThrow(id)

        // THEN
        assertEquals(id, taskStatusId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for ID below min value`() {
        // GIVEN
        val id = TaskStatusId.MIN_VALUE - 5

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskStatusId.createOrThrow(id)
        }
    }
}
