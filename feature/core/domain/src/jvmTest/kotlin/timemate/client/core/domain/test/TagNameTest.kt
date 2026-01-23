package timemate.client.core.domain.test

import timemate.client.core.domain.TagName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TagNameTest {

    @Test
    fun `create returns Success for valid name`() {
        // GIVEN
        val validName = "My Tag"

        // WHEN
        val result = TagName.create(validName)

        // THEN
        assertIs<TagName.CreationResult.Success>(result)
        assertEquals(validName, result.tagName.string)
    }

    @Test
    fun `create returns Success for name with minimum length`() {
        // GIVEN
        val minName = "A"

        // WHEN
        val result = TagName.create(minName)

        // THEN
        assertIs<TagName.CreationResult.Success>(result)
        assertEquals(minName, result.tagName.string)
    }

    @Test
    fun `create returns Success for name with maximum length`() {
        // GIVEN
        val maxName = "a".repeat(TagName.MAX_LENGTH)

        // WHEN
        val result = TagName.create(maxName)

        // THEN
        assertIs<TagName.CreationResult.Success>(result)
        assertEquals(maxName, result.tagName.string)
    }

    @Test
    fun `create returns Empty for empty name`() {
        // GIVEN
        val emptyName = ""

        // WHEN
        val result = TagName.create(emptyName)

        // THEN
        assertIs<TagName.CreationResult.Empty>(result)
    }

    @Test
    fun `create returns TooLong for name exceeding max length`() {
        // GIVEN
        val tooLongName = "a".repeat(TagName.MAX_LENGTH + 1)

        // WHEN
        val result = TagName.create(tooLongName)

        // THEN
        assertIs<TagName.CreationResult.TooLong>(result)
    }

    @Test
    fun `createOrThrow returns TagName for valid name`() {
        // GIVEN
        val name = "Valid Tag Name"

        // WHEN
        val tagName = TagName.createOrThrow(name)

        // THEN
        assertEquals(name, tagName.string)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for empty name`() {
        // GIVEN
        val invalidName = ""

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TagName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for too long name`() {
        // GIVEN
        val invalidName = "x".repeat(1000)

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TagName.createOrThrow(invalidName)
        }
    }

    @Test
    fun `create returns Success for alphanumeric and symbols within limit`() {
        // GIVEN
        val input = "Tag#42: Übung_@home!"

        // WHEN
        val result = TagName.create(input)

        // THEN
        assertIs<TagName.CreationResult.Success>(result)
        assertEquals(input, result.tagName.string)
    }

    @Test
    fun `create returns Empty for only whitespaces if length is invalid`() {
        // GIVEN
        val input = " ".repeat(0) // Empty string, not just whitespaces

        // WHEN
        val result = TagName.create(input)

        // THEN
        assertIs<TagName.CreationResult.Empty>(result)
    }

    @Test
    fun `create returns Success for single space character`() {
        // GIVEN
        val input = " "

        // WHEN
        val result = TagName.create(input)

        // THEN
        assertIs<TagName.CreationResult.Success>(result)
        assertEquals(input, result.tagName.string)
    }
}
