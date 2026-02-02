package timemate.tasks.domain.unittest

import timemate.tasks.domain.Task
import timemate.tasks.domain.TaskStatus
import timemate.tasks.domain.TaskDescription
import timemate.tasks.domain.TaskId
import timemate.tasks.domain.TaskName
import timemate.tasks.domain.TaskTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertNotSame
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlin.uuid.Uuid

class TaskTest {

    private val baseId = TaskId(Uuid.random())
    private val baseName = TaskName.createOrThrow("Test Task")
    private val baseDescription = TaskDescription.createOrThrow("A description")

    private val creationTime = Instant.parse("2024-01-01T00:00:00Z")
    private val dueTime = Instant.parse("2024-01-02T00:00:00Z")

    @Test
    fun `createOrThrow should throw if creationTime is after dueTime`() {
        // GIVEN
        val badCreation = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            Task.createOrThrow(
                id = baseId,
                name = baseName,
                description = baseDescription,
                creationTime = badCreation,
                dueTime = dueTime,
                tags = emptyList(),
            )
        }
    }

    @Test
    fun `dueIn returns correct duration when task is due`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = listOf(TaskTag.createOrThrow("Test")),
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
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = listOf(TaskTag.createOrThrow("Test")),
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
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )
        val currentTime = Instant.parse("2024-01-01T12:00:00Z")

        // WHEN
        val result = task.isDue(currentTime)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `isDue returns false if task is overdue`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isDue(currentTime)

        // THEN
        assertFalse(result)
    }

    @Test
    fun `isOverdue returns true if currentTime after dueTime and status not Done`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isOverdue(currentTime)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `isOverdue returns false if currentTime after dueTime but status Done`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            status = TaskStatus.Builtin.Done,
            tags = emptyList(),
        )
        val currentTime = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = task.isOverdue(currentTime)

        // THEN
        assertFalse(result)
    }

    @Test
    fun `markAs returns same instance if status unchanged`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )

        // WHEN
        val result = task.markAs(TaskStatus.Builtin.Planned)

        // THEN
        assertEquals(task, result)
        assertSame(task, result)
    }

    @Test
    fun `markAs returns new instance with updated status`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )
        val newStatus = TaskStatus.Builtin.Done

        // WHEN
        val result = task.markAs(newStatus)

        // THEN
        assertEquals(newStatus, result.status)
        assertNotSame(task, result)
    }

    @Test
    fun `markAsDone returns task marked as Done`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            status = TaskStatus.Builtin.Planned,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )

        // WHEN
        val doneTask = task.markAsDone()

        // THEN
        assertEquals(TaskStatus.Builtin.Done, doneTask.status)
        assertNotSame(task, doneTask)
    }

    @Test
    fun `create returns Success for valid time range`() {
        // GIVEN
        val validCreation = Instant.parse("2024-01-01T00:00:00Z")
        val validDue = Instant.parse("2024-01-02T00:00:00Z")

        // WHEN
        val result = Task.create(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = validCreation,
            dueTime = validDue,
            tags = listOf(TaskTag.createOrThrow("Test")),
        )

        // THEN
        assertIs<Task.CreationResult.Success>(result)
        assertEquals(validCreation, result.task.creationTime)
        assertEquals(validDue, result.task.dueTime)
    }

    @Test
    fun `create returns InvalidTimeRange if creationTime is after dueTime`() {
        // GIVEN
        val badCreation = Instant.parse("2024-01-03T00:00:00Z")

        // WHEN
        val result = Task.create(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = badCreation,
            dueTime = dueTime,
            tags = emptyList(),
        )

        // THEN
        assertIs<Task.CreationResult.InvalidTimeRange>(result)
    }

    @Test
    fun `rename returns new instance with updated name`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = emptyList()
        )
        val newName = TaskName.createOrThrow("New Name")

        // WHEN
        val result = task.rename(newName)

        // THEN
        assertEquals(newName, result.name)
        assertNotSame(task, result)
    }

    @Test
    fun `changeDescription returns new instance with updated description`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = emptyList()
        )
        val newDescription = TaskDescription.createOrThrow("New Description")

        // WHEN
        val result = task.changeDescription(newDescription)

        // THEN
        assertEquals(newDescription, result.description)
        assertNotSame(task, result)
    }

    @Test
    fun `changeDueTime returns Success with updated due time`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = emptyList()
        )
        val newDueTime = dueTime.plus(1.days)

        // WHEN
        val result = task.changeDueTime(newDueTime)

        // THEN
        assertIs<Task.ChangeDueTimeResult.Success>(result)
        assertEquals(newDueTime, result.task.dueTime)
        assertNotSame(task, result.task)
    }

    @Test
    fun `changeDueTime returns InvalidTimeRange if new due time is before creation time`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = emptyList()
        )
        val newDueTime = creationTime.minus(1.days)

        // WHEN
        val result = task.changeDueTime(newDueTime)

        // THEN
        assertIs<Task.ChangeDueTimeResult.InvalidTimeRange>(result)
    }

    @Test
    fun `updateTags returns new instance with updated tags`() {
        // GIVEN
        val task = Task.createOrThrow(
            id = baseId,
            name = baseName,
            description = baseDescription,
            creationTime = creationTime,
            dueTime = dueTime,
            tags = emptyList()
        )
        val newTags = listOf(TaskTag.createOrThrow("new-tag"))

        // WHEN
        val result = task.updateTags(newTags)

        // THEN
        assertEquals(newTags, result.tags)
        assertNotSame(task, result)
    }
}
