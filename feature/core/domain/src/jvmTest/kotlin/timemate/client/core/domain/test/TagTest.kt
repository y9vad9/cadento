package timemate.client.core.domain.test

import timemate.client.core.domain.Tag
import timemate.client.core.domain.TagId
import timemate.client.core.domain.TagName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

class TagTest {

    @Test
    fun `rename returns new tag with updated name and same id and time`() {
        // GIVEN
        val originalName = TagName.createOrThrow("Work")
        val newName = TagName.createOrThrow("Focus")
        val tagId = TagId.createOrThrow(1L)
        val createdAt = Clock.System.now()
        val original = Tag(
            id = tagId,
            name = originalName,
            creationTime = createdAt,
        )

        // WHEN
        val renamed = original.rename(newName)

        // THEN
        assertEquals(
            expected = tagId,
            actual = renamed.id,
            message = "ID should remain unchanged after rename",
        )
        assertEquals(
            expected = newName,
            actual = renamed.name,
            message = "Name should be updated to the new value",
        )
        assertEquals(
            expected = createdAt,
            actual = renamed.creationTime,
            message = "Creation time should remain unchanged after rename",
        )
    }
}
