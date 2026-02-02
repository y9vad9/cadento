package timemate.tasks.database.unittest

import timemate.tasks.database.TaskRow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TaskRowTest {

    @Test
    fun `equals with identical rows returns true`() {
        // GIVEN two identical rows
        val id = byteArrayOf(1, 2, 3)
        val row1 = TaskRow(
            id = id,
            name = "Task",
            description = "Desc",
            createdAt = "2024-01-01",
            due = "2024-01-02",
            statusId = 1L,
            status = "Planned",
        )
        val row2 = TaskRow(
            id = id.copyOf(),
            name = "Task",
            description = "Desc",
            createdAt = "2024-01-01",
            due = "2024-01-02",
            statusId = 1L,
            status = "Planned",
        )

        // THEN they should be equal
        assertEquals(
            expected = row1,
            actual = row2,
        )
    }

    @Test
    fun `equals with different ids returns false`() {
        // GIVEN rows with different ids
        val row1 = createTaskRow(id = byteArrayOf(1))
        val row2 = createTaskRow(id = byteArrayOf(2))

        // THEN they should not be equal
        assertNotEquals(
            illegal = row1,
            actual = row2,
        )
    }

    @Test
    fun `equals with different fields returns false`() {
        // GIVEN rows with different names
        val row1 = createTaskRow(name = "Task 1")
        val row2 = createTaskRow(name = "Task 2")

        // THEN they should not be equal
        assertNotEquals(
            illegal = row1,
            actual = row2,
        )
    }

    @Test
    fun `hashCode with identical rows returns same value`() {
        // GIVEN two identical rows
        val id = byteArrayOf(1, 2, 3)
        val row1 = createTaskRow(id = id)
        val row2 = createTaskRow(id = id.copyOf())

        // THEN their hashCodes should be equal
        assertEquals(
            expected = row1.hashCode(),
            actual = row2.hashCode(),
        )
    }

    @Test
    fun `hashCode with different rows returns different value`() {
        // GIVEN two different rows
        val row1 = createTaskRow(name = "Task 1")
        val row2 = createTaskRow(name = "Task 2")

        // THEN their hashCodes should be different
        assertNotEquals(
            illegal = row1.hashCode(),
            actual = row2.hashCode(),
        )
    }

    private fun createTaskRow(
        id: ByteArray = byteArrayOf(1),
        name: String = "Task",
        status: String = "Planned",
    ): TaskRow {
        return TaskRow(
            id = id,
            name = name,
            description = "Desc",
            createdAt = "2024-01-01",
            due = "2024-01-02",
            statusId = 1L,
            status = status,
        )
    }
}
