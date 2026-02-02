package timemate.core.domain.unittest

import timemate.core.domain.TagId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TagIdTest {

    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val id = 1L

        // WHEN
        val result = TagId.create(id)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(id, result.tagId.long)
    }

    @Test
    fun `create returns Success for zero ID`() {
        // GIVEN
        val id = 0L

        // WHEN
        val result = TagId.create(id)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(id, result.tagId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val id = -1L

        // WHEN
        val result = TagId.create(id)

        // THEN
        assertIs<TagId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow returns TagId for valid ID`() {
        // GIVEN
        val id = 100L

        // WHEN
        val tagId = TagId.createOrThrow(id)

        // THEN
        assertEquals(id, tagId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val id = -5L

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TagId.createOrThrow(id)
        }
    }

    @Test
    fun `create returns Success when value is equal to MINIMAL_VALUE`() {
        // GIVEN
        val value = TagId.MINIMAL_VALUE

        // WHEN
        val result = TagId.create(value)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(value, result.tagId.long)
    }

    @Test
    fun `create returns Negative for value less than MINIMAL_VALUE`() {
        // GIVEN
        val invalidValue = TagId.MINIMAL_VALUE - 1

        // WHEN
        val result = TagId.create(invalidValue)

        // THEN
        assertIs<TagId.CreationResult.Negative>(result)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for value less than MINIMAL_VALUE`() {
        // GIVEN
        val invalidValue = TagId.MINIMAL_VALUE - 1

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TagId.createOrThrow(invalidValue)
        }
    }

    @Test
    fun `long property should return value passed to constructor`() {
        // GIVEN
        val idValue = 111L
        val tagId = TagId.createOrThrow(idValue)

        // WHEN
        val result = tagId.long

        // THEN
        assertEquals(idValue, result)
    }
}
