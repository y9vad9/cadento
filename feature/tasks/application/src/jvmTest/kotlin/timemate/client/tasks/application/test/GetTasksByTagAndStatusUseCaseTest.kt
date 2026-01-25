package timemate.client.tasks.application.test

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.GetTasksByTagAndStatusUseCase
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetTasksByTagAndStatusUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = GetTasksByTagAndStatusUseCase(taskRepository)

    @Test
    fun `execute returns Success with filtered tasks`() = runTest {
        // GIVEN
        val tag = TaskTag.createOrThrow("tag1")
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(tag)
        )
        every { taskRepository.getTasks(any(), any()) } returns flowOf(listOf(task))

        // WHEN & THEN
        useCase.execute(tag = tag, status = TaskStatus.Builtin.Planned).test {
            val result = awaitItem()
            assertIs<GetTasksByTagAndStatusUseCase.Result.Success>(result)
            assertEquals(listOf(task), result.tasks)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute returns Error when repository throws exception`() = runTest {
        // GIVEN
        val exceptionMessage = "Database error"
        val exception = RuntimeException(exceptionMessage)
        every { taskRepository.getTasks(any(), any()) } returns flow { throw exception }

        // WHEN & THEN
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetTasksByTagAndStatusUseCase.Result.Error>(result)
            assertIs<RuntimeException>(result.error)
            assertEquals(exceptionMessage, result.error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
