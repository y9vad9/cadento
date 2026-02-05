package cadento.timers.domain.unittest

import cadento.timers.domain.LinkedTaskName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class LinkedTaskNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Task"

        // WHEN
        val result = LinkedTaskName.create(validName)

        // THEN
        assertIs<LinkedTaskName.CreationResult.Success>(result)
        assertEquals(validName, result.linkedTaskName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = LinkedTaskName.create(minName)

        // THEN
        assertIs<LinkedTaskName.CreationResult.Success>(result)
        assertEquals(minName, result.linkedTaskName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(100)

        // WHEN
        val result = LinkedTaskName.create(maxName)

        // THEN
        assertIs<LinkedTaskName.CreationResult.Success>(result)
        assertEquals(maxName, result.linkedTaskName.string)
    }

    @Test
    fun `create returns Empty for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = LinkedTaskName.create(emptyName)

        // THEN
        assertIs<LinkedTaskName.CreationResult.Empty>(result)
    }

    @Test
    fun `create returns TooLong for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(101)

        // WHEN
        val result = LinkedTaskName.create(tooLongName)

        // THEN
        assertIs<LinkedTaskName.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns LinkedTaskName for valid name`() {
        // GIVEN
        val name = "Valid Task Name"

        // WHEN
        val taskName = LinkedTaskName.createOrThrow(name)

        // THEN
        assertEquals(name, taskName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            LinkedTaskName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `string property should return value passed to constructor`() {
        // GIVEN
        val nameValue = "Linked Task Name"
        val linkedTaskName = LinkedTaskName.createOrThrow(nameValue)

        // WHEN
        val result = linkedTaskName.string

        // THEN
        assertEquals(nameValue, result)
    }
}
