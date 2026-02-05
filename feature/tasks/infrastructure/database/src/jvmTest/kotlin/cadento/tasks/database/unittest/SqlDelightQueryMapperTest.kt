package cadento.tasks.database.unittest

import cadento.tasks.database.SqlDelightQueryMapper
import cadento.tasks.database.TaskRow
import cadento.tasks.sqldelight.SelectDueTasksAsc
import cadento.tasks.sqldelight.SelectDueTasksDesc
import cadento.tasks.sqldelight.SelectTaskById
import cadento.tasks.sqldelight.SelectTasksByIds
import cadento.tasks.sqldelight.SelectTasksDueInRangeAsc
import cadento.tasks.sqldelight.SelectTasksFiltered
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlDelightQueryMapperTest {

    private val mapper = SqlDelightQueryMapper()
    private val taskId = "task-id".toByteArray()

    @Test
    fun `fromSelectTaskById with valid row returns correct TaskRow`() {
        // GIVEN a SelectTaskById result
        val row = SelectTaskById(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectTaskById(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }

    @Test
    fun `fromSelectTasksByIds with valid row returns correct TaskRow`() {
        // GIVEN a SelectTasksByIds result
        val row = SelectTasksByIds(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectTasksByIds(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }

    @Test
    fun `fromSelectDueTasksAsc with valid row returns correct TaskRow`() {
        // GIVEN a SelectDueTasksAsc result
        val row = SelectDueTasksAsc(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectDueTasksAsc(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }

    @Test
    fun `fromSelectDueTasksDesc with valid row returns correct TaskRow`() {
        // GIVEN a SelectDueTasksDesc result
        val row = SelectDueTasksDesc(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectDueTasksDesc(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }

    @Test
    fun `fromSelectTasksDueInRangeAsc with valid row returns correct TaskRow`() {
        // GIVEN a SelectTasksDueInRangeAsc result
        val row = SelectTasksDueInRangeAsc(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectTasksDueInRangeAsc(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }

    @Test
    fun `fromSelectTasksFiltered with valid row returns correct TaskRow`() {
        // GIVEN a SelectTasksFiltered result
        val row = SelectTasksFiltered(
            id = taskId,
            name = "Name",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
        )

        // WHEN we map it
        val result = mapper.fromSelectTasksFiltered(row)

        // THEN it matches expected TaskRow
        assertEquals(
            expected = TaskRow(
                id = taskId,
                name = "Name",
                description = "Desc",
                createdAt = "2024-01-01T00:00:00Z",
                due = "2024-01-02T00:00:00Z",
                statusId = 1L,
                status = "Planned",
            ),
            actual = result,
        )
    }
}
