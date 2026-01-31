package timemate.tasks.database.test

import kotlin.test.Test
import kotlin.test.assertEquals
import timemate.tasks.database.SqlDelightTaskMapper
import timemate.tasks.database.TaskRow
import kotlin.uuid.Uuid

class SqlDelightTaskMapperTest {

    private val mapper = SqlDelightTaskMapper()

    @Test
    fun `map with single TaskRow and tags returns correct DbTask`() {
        // GIVEN a database TaskRow and a list of tag strings
        val id = Uuid.random()
        val row = TaskRow(
            id = id.toByteArray(),
            name = "TaskName",
            description = "TaskDesc",
            createdAt = "2026-01-25T12:00:00Z",
            due = "2026-01-25T12:30:00Z",
            statusId = 1L,
            status = "Planned",
        )
        val tags = listOf("tag1", "tag2")

        // WHEN we map to infrastructure DbTask
        val result = mapper.map(
            row = row,
            tags = tags,
        )

        // THEN result DbTask fields match input
        assertEquals(
            expected = id,
            actual = result.id,
        )
        assertEquals(
            expected = "TaskName",
            actual = result.name,
        )
        assertEquals(
            expected = "TaskDesc",
            actual = result.description,
        )
        assertEquals(
            expected = "2026-01-25T12:00:00Z",
            actual = result.createdAt,
        )
        assertEquals(
            expected = "2026-01-25T12:30:00Z",
            actual = result.due,
        )
        assertEquals(
            expected = 1L,
            actual = result.statusId,
        )
        assertEquals(
            expected = "Planned",
            actual = result.status,
        )
        assertEquals(
            expected = tags,
            actual = result.tags,
        )
    }

    @Test
    fun `mapAll with multiple TaskRows returns correct list of DbTasks`() {
        // GIVEN multiple TaskRows and a map of tags by task id
        val id1 = Uuid.random()
        val id2 = Uuid.random()

        val row1 = TaskRow(
            id = id1.toByteArray(),
            name = "Task1",
            description = "Desc1",
            createdAt = "2026-01-25T10:00:00Z",
            due = "2026-01-25T10:30:00Z",
            statusId = 2L,
            status = "InProgress",
        )
        val row2 = TaskRow(
            id = id2.toByteArray(),
            name = "Task2",
            description = "Desc2",
            createdAt = "2026-01-25T11:00:00Z",
            due = "2026-01-25T11:30:00Z",
            statusId = 3L,
            status = "Completed",
        )

        val tagsByTaskId = mapOf(
            id1 to listOf("t1"),
            id2 to listOf("t2", "t3"),
        )

        // WHEN we map all rows
        val resultList = mapper.mapAll(
            rows = listOf(row1, row2),
            tagsByTaskId = tagsByTaskId,
        )

        // THEN resulting list contains correctly mapped DbTasks
        assertEquals(
            expected = 2,
            actual = resultList.size,
        )

        val mapped1 = resultList.first { it.id == id1 }
        assertEquals(
            expected = "Task1",
            actual = mapped1.name,
        )
        assertEquals(
            expected = listOf("t1"),
            actual = mapped1.tags,
        )

        val mapped2 = resultList.first { it.id == id2 }
        assertEquals(
            expected = "Task2",
            actual = mapped2.name,
        )
        assertEquals(
            expected = listOf("t2", "t3"),
            actual = mapped2.tags,
        )
    }

    @Test
    fun `mapAll with missing tags for some tasks returns DbTasks with empty tags list`() {
        // GIVEN a TaskRow with no entry in the tags map
        val id = Uuid.random()
        val row = TaskRow(
            id = id.toByteArray(),
            name = "NoTags",
            description = "Desc",
            createdAt = "2026-01-25T12:00:00Z",
            due = "2026-01-25T12:30:00Z",
            statusId = 1L,
            status = "Planned",
        )
        val tagsByTaskId = emptyMap<Uuid, List<String>>()

        // WHEN we map the rows
        val resultList = mapper.mapAll(
            rows = listOf(row),
            tagsByTaskId = tagsByTaskId,
        )

        // THEN resulting DbTask has an empty tags list
        assertEquals(
            expected = 1,
            actual = resultList.size,
        )
        assertEquals(
            expected = emptyList(),
            actual = resultList.first().tags,
        )
    }
}
