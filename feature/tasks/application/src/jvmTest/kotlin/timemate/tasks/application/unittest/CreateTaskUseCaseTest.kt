package timemate.tasks.application.unittest

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import timemate.tasks.application.CreateTaskUseCase
import timemate.tasks.application.TaskRepository
import timemate.tasks.domain.Task
import timemate.tasks.domain.TaskDescription
import timemate.tasks.domain.TaskId
import timemate.tasks.domain.TaskName
import timemate.tasks.domain.TaskStatus
import timemate.tasks.domain.TaskTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import kotlin.uuid.Uuid

class CreateTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = CreateTaskUseCase(taskRepository)

    @Test
    fun `execute with valid task returns Success`() = runTest {
        // GIVEN a valid task and repository that succeeds
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("tag1")),
        )
        coEvery { taskRepository.createTask(task) } returns Result.success(Unit)

        // WHEN we execute use case
        val result = useCase.execute(task)

        // THEN result is success with same task
        assertIs<CreateTaskUseCase.Result.Success>(result)
        assertEquals(
            expected = task,
            actual = result.task,
        )
        coVerify { taskRepository.createTask(task) }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN a task and repository that fails
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList(),
        )
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.createTask(task) } returns Result.failure(exception)

        // WHEN we execute use case
        val result = useCase.execute(task)

        // THEN result is error with caught exception
        assertIs<CreateTaskUseCase.Result.Error>(result)
        assertEquals(
            expected = exception,
            actual = result.error,
        )
    }
}
