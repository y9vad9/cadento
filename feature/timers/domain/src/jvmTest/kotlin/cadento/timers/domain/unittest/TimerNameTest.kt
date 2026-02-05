package cadento.timers.domain.unittest

import cadento.timers.domain.TimerName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TimerNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Timer"

        // WHEN
        val result = TimerName.create(validName)

        // THEN
        assertIs<TimerName.CreationResult.Success>(result)
        assertEquals(validName, result.timerName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = TimerName.create(minName)

        // THEN
        assertIs<TimerName.CreationResult.Success>(result)
        assertEquals(minName, result.timerName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(50)

        // WHEN
        val result = TimerName.create(maxName)

        // THEN
        assertIs<TimerName.CreationResult.Success>(result)
        assertEquals(maxName, result.timerName.string)
    }

    @Test
    fun `create returns Empty for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = TimerName.create(emptyName)

        // THEN
        assertIs<TimerName.CreationResult.Empty>(result)
    }

    @Test
    fun `create returns TooLong for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(51)

        // WHEN
        val result = TimerName.create(tooLongName)

        // THEN
        assertIs<TimerName.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns TimerName for valid name`() {
        // GIVEN
        val name = "Valid Name"

        // WHEN
        val timerName = TimerName.createOrThrow(name)

        // THEN
        assertEquals(name, timerName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TimerName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `string property should return value passed to constructor`() {
        // GIVEN
        val nameValue = "Test Timer"
        val timerName = TimerName.createOrThrow(nameValue)

        // WHEN
        val result = timerName.string

        // THEN
        assertEquals(nameValue, result)
    }
}
