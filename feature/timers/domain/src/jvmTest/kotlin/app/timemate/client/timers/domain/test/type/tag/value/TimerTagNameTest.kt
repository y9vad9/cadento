package app.timemate.client.timers.domain.test.type.tag.value

import app.timemate.client.timers.domain.type.tag.value.TimerTagName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TimerTagNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Tag"

        // WHEN
        val result = TimerTagName.create(validName)

        // THEN
        assertIs<TimerTagName.CreationResult.Success>(result)
        assertEquals(validName, result.timerTagName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = TimerTagName.create(minName)

        // THEN
        assertIs<TimerTagName.CreationResult.Success>(result)
        assertEquals(minName, result.timerTagName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(50)

        // WHEN
        val result = TimerTagName.create(maxName)

        // THEN
        assertIs<TimerTagName.CreationResult.Success>(result)
        assertEquals(maxName, result.timerTagName.string)
    }

    @Test
    fun `create returns Empty for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = TimerTagName.create(emptyName)

        // THEN
        assertIs<TimerTagName.CreationResult.Empty>(result)
    }

    @Test
    fun `create returns TooLong for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(51)

        // WHEN
        val result = TimerTagName.create(tooLongName)

        // THEN
        assertIs<TimerTagName.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns TimerTagName for valid name`() {
        // GIVEN
        val name = "Valid Tag Name"

        // WHEN
        val tagName = TimerTagName.createOrThrow(name)

        // THEN
        assertEquals(name, tagName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TimerTagName.createOrThrow(invalidName)
        }
    }
}
