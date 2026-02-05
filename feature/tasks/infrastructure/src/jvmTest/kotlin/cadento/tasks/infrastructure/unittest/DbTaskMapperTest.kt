package cadento.tasks.infrastructure.unittest

import cadento.tasks.application.TaskFilter
import cadento.tasks.application.TaskSort
import cadento.tasks.database.DbTask
import cadento.tasks.database.DbTaskFilter
import cadento.tasks.database.DbTaskSort
import cadento.tasks.domain.TaskStatus
import cadento.tasks.domain.TaskTag
import cadento.tasks.infrastructure.DbTaskMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class DbTaskMapperTest {

    private val mapper = DbTaskMapper()

    @Test
    fun `mapFilter with all fields returns correct DbTaskFilter`() {
        // GIVEN a TaskFilter with all fields set
        val dueBefore = Instant.parse("2024-01-01T00:00:00Z")
        val tag = TaskTag.createOrThrow("tag")
        val status = TaskStatus.Builtin.Planned
        val filter = TaskFilter(
            dueBefore = dueBefore,
            tag = tag,
            status = status,
        )

        // WHEN we map to infrastructure filter
        val result = mapper.mapFilter(filter)

        // THEN all fields are correctly transformed to database types
        assertEquals(
            expected = DbTaskFilter(
                dueBefore = dueBefore.toString(),
                tag = tag.string,
                statusId = status.id.long,
            ),
            actual = result,
        )
    }

    @Test
    fun `mapFilter with null fields returns DbTaskFilter with nulls`() {
        // GIVEN an empty TaskFilter
        val filter = TaskFilter()

        // WHEN we map to infrastructure filter
        val result = mapper.mapFilter(filter)

        // THEN all fields in result are null
        assertEquals(
            expected = DbTaskFilter(
                dueBefore = null,
                tag = null,
                statusId = null,
            ),
            actual = result,
        )
    }

    @Test
    fun `mapSort with all variants returns correct DbTaskSort variants`() {
        // GIVEN all possible domain sort variants
        val inputs = listOf(
            TaskSort.ByCreationTimeAsc,
            TaskSort.ByCreationTimeDesc,
            TaskSort.ByDueTimeAsc,
            TaskSort.ByDueTimeDesc,
        )

        // WHEN we map each to infrastructure sort
        val results = inputs.map { mapper.mapSort(it) }

        // THEN result variants match database sort enum
        val expected = listOf(
            DbTaskSort.ByCreationAsc,
            DbTaskSort.ByCreationDesc,
            DbTaskSort.ByDueAsc,
            DbTaskSort.ByDueDesc,
        )
        assertEquals(
            expected = expected,
            actual = results,
        )
    }

    @Test
    fun `mapToDomain with valid DbTask returns correct domain Task`() {
        // GIVEN a DbTask with valid fields
        val id = Uuid.random()
        val dbTask = DbTask(
            id = id,
            name = "Task Name",
            description = "Task Description",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
            tags = listOf("tag1", "tag2"),
        )

        // WHEN we map to domain Task
        val result = mapper.mapToDomain(dbTask)

        // THEN all domain fields match database input
        assertEquals(
            expected = id,
            actual = result.id.value,
        )
        assertEquals(
            expected = "Task Name",
            actual = result.name.string,
        )
        assertEquals(
            expected = "Task Description",
            actual = result.description.string,
        )
        assertEquals(
            expected = Instant.parse("2024-01-01T00:00:00Z"),
            actual = result.creationTime,
        )
        assertEquals(
            expected = Instant.parse("2024-01-02T00:00:00Z"),
            actual = result.dueTime,
        )
        assertEquals(
            expected = 1L,
            actual = result.status.id.long,
        )
        assertEquals(
            expected = "Planned",
            actual = result.status.name.string,
        )
        assertEquals(
            expected = listOf("tag1", "tag2"),
            actual = result.tags.map { it.string },
        )
    }
}
