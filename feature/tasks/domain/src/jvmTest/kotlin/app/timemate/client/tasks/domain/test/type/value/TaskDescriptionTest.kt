package app.timemate.client.tasks.domain.test.type.value

import app.timemate.client.tasks.domain.type.value.TaskDescription
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TaskDescriptionTest {

    @Test
    fun `create returns Success for valid description`() {
        // GIVEN
        val validDescription = "This is a valid task description."

        // WHEN
        val result = TaskDescription.create(validDescription)

        // THEN
        assertIs<TaskDescription.CreationResult.Success>(result)
        assertEquals(validDescription, result.description.string)
    }

    @Test
    fun `create returns Success for description with minimum length`() {
        // GIVEN
        val minDescription = "A"

        // WHEN
        val result = TaskDescription.create(minDescription)

        // THEN
        assertIs<TaskDescription.CreationResult.Success>(result)
        assertEquals(minDescription, result.description.string)
    }

    @Test
    fun `create returns Success for description with maximum length`() {
        // GIVEN
        val maxDescription = "a".repeat(10000)

        // WHEN
        val result = TaskDescription.create(maxDescription)

        // THEN
        assertIs<TaskDescription.CreationResult.Success>(result)
        assertEquals(maxDescription, result.description.string)
    }

    @Test
    fun `create returns InvalidLength for empty description`() {
        // GIVEN
        val emptyDescription = ""

        // WHEN
        val result = TaskDescription.create(emptyDescription)

        // THEN
        assertIs<TaskDescription.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `create returns InvalidLength for description exceeding max length`() {
        // GIVEN
        val tooLongDescription = "a".repeat(10001)

        // WHEN
        val result = TaskDescription.create(tooLongDescription)

        // THEN
        assertIs<TaskDescription.CreationResult.InvalidLength>(result)
    }

    @Test
    fun `createOrThrow returns TaskDescription for valid description`() {
        // GIVEN
        val description = "Another valid description."

        // WHEN
        val taskDescription = TaskDescription.createOrThrow(description)

        // THEN
        assertEquals(description, taskDescription.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid description`() {
        // GIVEN
        val invalidDescription = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TaskDescription.createOrThrow(invalidDescription)
        }
    }
}
