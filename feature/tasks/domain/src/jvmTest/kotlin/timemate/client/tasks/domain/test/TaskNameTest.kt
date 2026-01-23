package timemate.client.tasks.domain.test

import timemate.client.tasks.domain.TaskName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TaskNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Task"

        // WHEN
        val result = TaskName.create(validName)

        // THEN
        assertIs<TaskName.CreationResult.Success>(result)
        assertEquals(validName, result.taskName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = TaskName.create(minName)

        // THEN
        assertIs<TaskName.CreationResult.Success>(result)
        assertEquals(minName, result.taskName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(100)

        // WHEN
        val result = TaskName.create(maxName)

        // THEN
        assertIs<TaskName.CreationResult.Success>(result)
        assertEquals(maxName, result.taskName.string)
    }

    @Test
    fun `create returns InvalidLength for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = TaskName.create(emptyName)

        // THEN
        assertIs<TaskName.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `create returns InvalidLength for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(101)

        // WHEN
        val result = TaskName.create(tooLongName)

        // THEN
        assertIs<TaskName.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `createOrThrow returns TaskName for valid name`() {
        // GIVEN
        val name = "Valid Task Name"

        // WHEN
        val taskName = TaskName.createOrThrow(name)

        // THEN
        assertEquals(name, taskName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `string property should return value passed to constructor`() {
        // GIVEN
        val nameValue = "Test Task"
        val taskName = TaskName.createOrThrow(nameValue)

        // WHEN
        val result = taskName.string

        // THEN
        assertEquals(nameValue, result)
    }
}
