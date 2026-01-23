package timemate.client.tasks.domain.test

import timemate.client.tasks.domain.TaskStatusId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertFalse

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

    @Test
    fun `predefined constants have expected values`() {
        // GIVEN / WHEN / THEN
        assertEquals(-4L, TaskStatusId.PLANNED.long)
        assertEquals(-3L, TaskStatusId.IN_PROGRESS.long)
        assertEquals(-2L, TaskStatusId.PAUSED.long)
        assertEquals(-1L, TaskStatusId.DONE.long)
    }

    @Test
    fun `isBuiltin returns true for built-in TaskStatusIds`() {
        // GIVEN
        val builtIns = listOf(
            TaskStatusId.PLANNED,
            TaskStatusId.IN_PROGRESS,
            TaskStatusId.PAUSED,
            TaskStatusId.DONE
        )

        // WHEN / THEN
        builtIns.forEach { id ->
            assertTrue(id.isBuiltin(), "Expected $id to be builtin")
        }
    }

    @Test
    fun `isBuiltin returns false for non built-in TaskStatusId`() {
        // GIVEN
        val customId = TaskStatusId.createOrThrow(0L)

        // WHEN / THEN
        assertFalse(customId.isBuiltin())
    }

    @Test
    fun `isNotBuiltin returns true for non built-in TaskStatusId`() {
        // GIVEN
        val customId = TaskStatusId.createOrThrow(0L)

        // WHEN / THEN
        assertTrue(customId.isNotBuiltin())
    }

    @Test
    fun `isNotBuiltin returns false for built-in TaskStatusId`() {
        // GIVEN
        val builtIn = TaskStatusId.PLANNED

        // WHEN / THEN
        assertFalse(builtIn.isNotBuiltin())
    }
}
