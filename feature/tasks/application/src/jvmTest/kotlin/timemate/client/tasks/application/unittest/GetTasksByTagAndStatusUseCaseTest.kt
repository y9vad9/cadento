package timemate.client.tasks.application.unittest

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
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetTasksByTagAndStatusUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = GetTasksByTagAndStatusUseCase(
        taskRepository = taskRepository,
        clock = clock,
    )

    @Test
    fun `execute with tag and status returns Success with filtered tasks`() = runTest {
        // GIVEN tag, status and repository that returns a match
        val tag = TaskTag.createOrThrow("tag1")
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = now,
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(tag),
        )
        every { clock.now() } returns now
        every { taskRepository.observeTasks(any(), any(), now) } returns flowOf(listOf(task))

        // WHEN & THEN we collect results
        useCase.execute(tag = tag, status = TaskStatus.Builtin.Planned).test {
            val result = awaitItem()
            assertIs<GetTasksByTagAndStatusUseCase.Result.Success>(result)
            assertEquals(
                expected = listOf(task),
                actual = result.tasks,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN fixed time and repository that throws exception
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val exceptionMessage = "Database error"
        val exception = RuntimeException(exceptionMessage)
        every { clock.now() } returns now
        every { taskRepository.observeTasks(any(), any(), now) } returns flow { throw exception }

        // WHEN & THEN we collect results
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetTasksByTagAndStatusUseCase.Result.Error>(result)
            assertIs<RuntimeException>(result.error)
            assertEquals(
                expected = exceptionMessage,
                actual = result.error.message,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
