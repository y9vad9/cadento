package timemate.tasks.infrastructure.unittest

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.TaskFilter
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.application.TaskSort
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
import timemate.tasks.database.DbTask
import timemate.tasks.database.DbTaskFilter
import timemate.tasks.database.DbTaskPatch
import timemate.tasks.database.DbTaskSort
import timemate.tasks.database.TasksDatabaseSource
import timemate.tasks.infrastructure.DbTaskMapper
import timemate.tasks.infrastructure.TaskRepositoryImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid

class TaskRepositoryImplTest {

    private val databaseSource: TasksDatabaseSource = mockk()
    private val dbTaskMapper: DbTaskMapper = mockk()
    private val repository: TaskRepository = TaskRepositoryImpl(
        databaseSource = databaseSource,
        dbTaskMapper = dbTaskMapper,
    )

    @Test
    fun `createTask with valid task returns Success`() = runTest {
        // GIVEN a task and database source that succeeds
        val task = createTask()
        coEvery {
            databaseSource.insertTask(
                task = any(),
            )
        } returns Unit

        // WHEN we call createTask
        val result = repository.createTask(task)

        // THEN result is success and database was updated
        assertTrue(result.isSuccess)
        coVerify {
            databaseSource.insertTask(
                task = DbTask(
                    id = task.id.value,
                    name = task.name.string,
                    description = task.description.string,
                    createdAt = task.creationTime.toString(),
                    due = task.dueTime.toString(),
                    statusId = task.status.id.long,
                    status = task.status.name.string,
                    tags = task.tags.map { it.string },
                ),
            )
        }
    }

    @Test
    fun `createTask with source failure returns failure`() = runTest {
        // GIVEN a task and database source that fails
        val task = createTask()
        val exception = RuntimeException("DB Error")
        coEvery {
            databaseSource.insertTask(any())
        } throws exception

        // WHEN we call createTask
        val result = repository.createTask(task)

        // THEN result is failure with same exception
        assertTrue(result.isFailure)
        assertEquals(
            expected = exception,
            actual = result.exceptionOrNull(),
        )
    }

    @Test
    fun `updateTask with valid task returns Success and updated task`() = runTest {
        // GIVEN a task and database source that succeeds
        val task = createTask()
        coEvery {
            databaseSource.updateTask(
                id = any(),
                patch = any(),
            )
        } returns Unit

        // WHEN we call updateTask
        val result = repository.updateTask(task)

        // THEN result is success with updated task instance
        assertTrue(result.isSuccess)
        assertEquals(
            expected = task,
            actual = result.getOrNull(),
        )
        coVerify {
            databaseSource.updateTask(
                id = task.id.value,
                patch = DbTaskPatch(
                    name = task.name.string,
                    description = task.description.string,
                    due = task.dueTime.toString(),
                    statusId = task.status.id.long,
                    tags = task.tags.map { it.string },
                ),
            )
        }
    }

    @Test
    fun `deleteTask with taskId returns Success`() = runTest {
        // GIVEN task id and database source that succeeds
        val taskId = TaskId(Uuid.random())
        coEvery { databaseSource.deleteTasks(any()) } returns Unit

        // WHEN we call deleteTask
        val result = repository.deleteTask(taskId)

        // THEN result is success and database was updated
        assertTrue(result.isSuccess)
        coVerify { databaseSource.deleteTasks(listOf(taskId.value)) }
    }

    @Test
    fun `deleteTasks with multiple ids returns Success`() = runTest {
        // GIVEN list of task ids and database source that succeeds
        val taskIds = listOf(TaskId(Uuid.random()), TaskId(Uuid.random()))
        coEvery { databaseSource.deleteTasks(any()) } returns Unit

        // WHEN we call deleteTasks
        val result = repository.deleteTasks(taskIds)

        // THEN result is success and database was updated
        assertTrue(result.isSuccess)
        coVerify { databaseSource.deleteTasks(taskIds.map { it.value }) }
    }

    @Test
    fun `observeTasks with filter and sort returns correct mapped tasks`() = runTest {
        // GIVEN filter, sort and database that returns a DbTask
        val filter = TaskFilter()
        val sort = TaskSort.ByDueTimeAsc
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val dbFilter = DbTaskFilter()
        val dbSort = DbTaskSort.ByDueAsc
        val dbTask = createDbTask()
        val task = createTask()

        every { dbTaskMapper.mapFilter(filter) } returns dbFilter
        every { dbTaskMapper.mapSort(sort) } returns dbSort
        every {
            databaseSource.observeTasks(dbFilter, dbSort, now.toString())
        } returns flowOf(listOf(dbTask))
        every { dbTaskMapper.mapToDomain(dbTask) } returns task

        // WHEN & THEN we collect results
        repository.observeTasks(filter, sort, now).test {
            val result = awaitItem()
            assertEquals(
                expected = listOf(task),
                actual = result,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTask returns correct mapped task`() = runTest {
        // GIVEN task id and database source that returns a flow of DbTask
        val taskId = TaskId(Uuid.random())
        val dbTask = createDbTask()
        val task = createTask()

        every { databaseSource.observeTask(taskId.value) } returns flowOf(dbTask)
        every { dbTaskMapper.mapToDomain(dbTask) } returns task

        // WHEN & THEN we collect results
        repository.observeTask(taskId).test {
            val result = awaitItem()
            assertEquals(
                expected = task,
                actual = result,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTask with existing id returns Success with mapped task`() = runTest {
        // GIVEN task id and database source that returns a DbTask
        val taskId = TaskId(Uuid.random())
        val dbTask = createDbTask()
        val task = createTask()

        coEvery { databaseSource.observeTask(taskId.value) } returns flowOf(dbTask)
        every { dbTaskMapper.mapToDomain(dbTask) } returns task

        // WHEN we call getTask
        val result = repository.getTask(taskId)

        // THEN result is success with mapped domain task
        assertTrue(result.isSuccess)
        assertEquals(
            expected = task,
            actual = result.getOrNull(),
        )
    }

    @Test
    fun `getTask with non-existent id returns Success with null`() = runTest {
        // GIVEN non-existent task id
        val taskId = TaskId(Uuid.random())
        coEvery { databaseSource.observeTask(taskId.value) } returns flowOf(null)

        // WHEN we call getTask
        val result = repository.getTask(taskId)

        // THEN result is success with null task
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `observeDueTasks with time returns correct mapped tasks`() = runTest {
        // GIVEN fixed time and database that returns due tasks
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val dbTask = createDbTask()
        val task = createTask()

        every { databaseSource.observeDueTasks(now.toString(), DbTaskSort.ByDueDesc) } returns flowOf(listOf(dbTask))
        every { dbTaskMapper.mapToDomain(dbTask) } returns task

        // WHEN & THEN we collect results
        repository.observeDueTasks(now).test {
            val result = awaitItem()
            assertEquals(
                expected = listOf(task),
                actual = result,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTasksDueBetween with range returns correct mapped tasks`() = runTest {
        // GIVEN time range and database that returns tasks in that range
        val range = Instant.parse("2024-01-01T00:00:00Z")..Instant.parse("2024-01-02T00:00:00Z")
        val dbTask = createDbTask()
        val task = createTask()

        every {
            databaseSource.observeTasksDueInRange(
                range.start.toString(),
                range.endInclusive.toString(),
            )
        } returns flowOf(listOf(dbTask))
        every { dbTaskMapper.mapToDomain(dbTask) } returns task

        // WHEN & THEN we collect results
        repository.observeTasksDueBetween(range).test {
            val result = awaitItem()
            assertEquals(
                expected = listOf(task),
                actual = result,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Helpers

    private fun createTask(): Task {
        return Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task"),
            description = TaskDescription.createOrThrow("Desc"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList(),
        )
    }

    private fun createDbTask(): DbTask {
        return DbTask(
            id = Uuid.random(),
            name = "Task",
            description = "Desc",
            createdAt = "2024-01-01T00:00:00Z",
            due = "2024-01-02T00:00:00Z",
            statusId = 1L,
            status = "Planned",
            tags = emptyList(),
        )
    }
}
