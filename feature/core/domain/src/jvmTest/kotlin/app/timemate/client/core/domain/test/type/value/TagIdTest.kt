package app.timemate.client.core.domain.test.type.value

import app.timemate.client.core.domain.type.value.TagId
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TagIdTest {
    @Test
    fun `create returns Success for valid positive ID`() {
        // GIVEN
        val validLong = Random.nextLong(TagId.MINIMAL_VALUE, Long.MAX_VALUE)

        // WHEN
        val result = TagId.create(validLong)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(validLong, result.tagId.long)
    }

    @Test
    fun `create returns Negative for negative ID`() {
        // GIVEN
        val negativeLong = TagId.MINIMAL_VALUE - 1

        // WHEN
        val result = TagId.create(negativeLong)

        // THEN
        assertIs<TagId.CreationResult.Negative>(result)
    }

    @Test
    fun `create succeeds for zero`() {
        // GIVEN
        val zero = TagId.MINIMAL_VALUE

        // WHEN
        val result = TagId.create(zero)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(zero, result.tagId.long)
    }

    @Test
    fun `create succeeds for Long MAX_VALUE`() {
        // GIVEN
        val maxValue = Long.MAX_VALUE

        // WHEN
        val result = TagId.create(maxValue)

        // THEN
        assertIs<TagId.CreationResult.Success>(result)
        assertEquals(maxValue, result.tagId.long)
    }

    @Test
    fun `createOrThrow returns TagId for valid ID`() {
        // GIVEN
        val validLong = Random.nextLong(TagId.MINIMAL_VALUE, Long.MAX_VALUE)

        // WHEN
        val tagId = TagId.createOrThrow(validLong)

        // THEN
        assertEquals(validLong, tagId.long)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for negative ID`() {
        // GIVEN
        val negativeLong = TagId.MINIMAL_VALUE - 1

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            TagId.createOrThrow(negativeLong)
        }
    }
}
