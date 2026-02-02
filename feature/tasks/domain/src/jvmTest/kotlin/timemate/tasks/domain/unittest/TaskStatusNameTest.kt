package timemate.tasks.domain.unittest

import timemate.tasks.domain.TaskStatusName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertContains

class TaskStatusNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Status"

        // WHEN
        val result = TaskStatusName.create(validName)

        // THEN
        assertIs<TaskStatusName.CreationResult.Success>(result)
        assertEquals(validName, result.taskStatusName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = TaskStatusName.create(minName)

        // THEN
        assertIs<TaskStatusName.CreationResult.Success>(result)
        assertEquals(minName, result.taskStatusName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(100)

        // WHEN
        val result = TaskStatusName.create(maxName)

        // THEN
        assertIs<TaskStatusName.CreationResult.Success>(result)
        assertEquals(maxName, result.taskStatusName.string)
    }

    @Test
    fun `create returns InvalidLength for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = TaskStatusName.create(emptyName)

        // THEN
        assertIs<TaskStatusName.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `create returns InvalidLength for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(101)

        // WHEN
        val result = TaskStatusName.create(tooLongName)

        // THEN
        assertIs<TaskStatusName.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `createOrThrow returns TaskStatusName for valid name`() {
        // GIVEN
        val name = "Valid Status Name"

        // WHEN
        val taskStatusName = TaskStatusName.createOrThrow(name)

        // THEN
        assertEquals(name, taskStatusName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskStatusName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `isBuiltin returns true for built-in names case-insensitive`() {
        // GIVEN
        val planned = TaskStatusName.createOrThrow("planned")
        val inProgress = TaskStatusName.createOrThrow("IN PROGRESS")
        val paused = TaskStatusName.createOrThrow("Paused")
        val done = TaskStatusName.createOrThrow("done")

        // WHEN / THEN
        assertEquals(true, planned.isBuiltin())
        assertEquals(true, inProgress.isBuiltin())
        assertEquals(true, paused.isBuiltin())
        assertEquals(true, done.isBuiltin())
    }

    @Test
    fun `isBuiltin returns false for non-built-in names`() {
        // GIVEN
        val customStatus = TaskStatusName.createOrThrow("Custom Status")

        // WHEN / THEN
        assertEquals(false, customStatus.isBuiltin())
    }

    @Test
    fun `isNotBuiltin returns true for non-built-in names`() {
        // GIVEN
        val customStatus = TaskStatusName.createOrThrow("Custom Status")

        // WHEN / THEN
        assertEquals(true, customStatus.isNotBuiltin())
    }

    @Test
    fun `isNotBuiltin returns false for built-in names`() {
        // GIVEN
        val planned = TaskStatusName.createOrThrow("Planned")

        // WHEN / THEN
        assertEquals(false, planned.isNotBuiltin())
    }

    @Test
    fun `string property should return value passed to constructor`() {
        // GIVEN
        val nameValue = "In Progress"
        val taskStatusName = TaskStatusName.createOrThrow(nameValue)

        // WHEN
        val result = taskStatusName.string

        // THEN
        assertEquals(nameValue, result)
    }

    @Test
    fun `BUILTIN_NAMES should contain all predefined TaskStatusNames`() {
        // WHEN
        val builtinNames = TaskStatusName.BUILTIN_NAMES

        // THEN
        assertEquals(4, builtinNames.size)
        assertContains(builtinNames, TaskStatusName.PLANNED)
        assertContains(builtinNames, TaskStatusName.IN_PROGRESS)
        assertContains(builtinNames, TaskStatusName.PAUSED)
        assertContains(builtinNames, TaskStatusName.DONE)
    }
}
