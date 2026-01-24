package timemate.client.tasks.application.test

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
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

class GetTasksByTagAndStatusUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = GetTasksByTagAndStatusUseCase(taskRepository)

    @Test
    fun `execute returns Success with filtered tasks`() = runTest {
        // GIVEN
        val tag = TaskTag.createOrThrow("tag1")
        val task = Task.createOrThrow(
            id = TaskId.createOrThrow(1L),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(tag)
        )
        every { taskRepository.getTasks(any(), any()) } returns flowOf(listOf(task))

        // WHEN
        val result = useCase.execute(tag = tag, status = TaskStatus.Builtin.Planned).first()

        // THEN
        assertIs<GetTasksByTagAndStatusUseCase.Result.Success>(result)
        assertEquals(listOf(task), result.tasks)
    }
}
