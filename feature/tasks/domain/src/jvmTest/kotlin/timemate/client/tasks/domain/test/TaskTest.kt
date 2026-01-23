package timemate.client.tasks.domain.test

import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class TaskTest {

    private val baseId = TaskId.createOrThrow(1L)
    private val baseName = TaskName.createOrThrow("Test Task")
    private val baseDescription = TaskDescription.createOrThrow("A description")

    private val creationTime = Instant.parse("2024-01-01T00:00:00Z")
    private val dueTime = Instant.parse("2024-01-02T00:00:00Z")

    @Test
    fun `init should throw if creationTime is after dueTime`() {
        // GIVEN
        val badCreation = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = baseId,
                name = baseName,
                description = baseDescription,
                creationTime = badCreation,
                dueTime = dueTime
            )
        }
    }

    @Test
    fun `dueIn returns correct duration when task is due`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime
        )
        val currentTime = Instant.parse("2024-01-01T12:00:00Z")

        // WHEN
        val dueDuration = task.dueIn(currentTime)

        // THEN
        val expected = dueTime - currentTime
        assertEquals(expected, dueDuration)
    }

    @Test
    fun `dueIn throws if task is overdue`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            task.dueIn(currentTime)
        }
    }

    @Test
    fun `isDue returns true if task is not overdue`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned
        )
        val currentTime = Instant.parse("2024-01-01T12:00:00Z")

        // WHEN
        val result = task.isDue(currentTime)

        // THEN
        assertEquals(true, result)
    }

    @Test
    fun `isDue returns false if task is overdue`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isDue(currentTime)

        // THEN
        assertEquals(false, result)
    }

    @Test
    fun `isOverdue returns true if currentTime after dueTime and status not Done`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isOverdue(currentTime)

        // THEN
        assertEquals(true, result)
    }

    @Test
    fun `isOverdue returns false if currentTime after dueTime but status Done`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Done
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isOverdue(currentTime)

        // THEN
        assertEquals(false, result)
    }

    @Test
    fun `markAs returns same instance if status unchanged`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime
        )

        // WHEN
        val result = task.markAs(TaskStatus.Builtin.Planned)

        // THEN
        assertEquals(task, result)
        assert(task === result)
    }

    @Test
    fun `markAs returns new instance with updated status`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime
        )
        val newStatus = TaskStatus.Builtin.Done

        // WHEN
        val result = task.markAs(newStatus)

        // THEN
        assertEquals(newStatus, result.status)
        assert(task !== result)
    }

    @Test
    fun `markAsDone returns task marked as Done`() {
        // GIVEN
        val task = Task(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime
        )

        // WHEN
        val doneTask = task.markAsDone()

        // THEN
        assertEquals(TaskStatus.Builtin.Done, doneTask.status)
        assert(task !== doneTask)
    }
}
