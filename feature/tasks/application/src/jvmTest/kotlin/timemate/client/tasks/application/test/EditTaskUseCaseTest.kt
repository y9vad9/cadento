package timemate.client.tasks.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.EditTaskUseCase
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

class EditTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = EditTaskUseCase(taskRepository)

    @Test
    fun `execute updates task and returns Success`() = runTest {
        // GIVEN
        val task = Task.createOrThrow(
            id = TaskId.createOrThrow(1L),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("tag1"))
        )
        coEvery { taskRepository.updateTask(task) } returns Unit

        // WHEN
        val result = useCase.execute(task)

        // THEN
        assertIs<EditTaskUseCase.Result.Success>(result)
        assertEquals(task, result.task)
        coVerify { taskRepository.updateTask(task) }
    }

    @Test
    fun `execute returns Error when repository fails`() = runTest {
        // GIVEN
        val task = Task.createOrThrow(
            id = TaskId.createOrThrow(1L),
            name = TaskName.createOrThrow("Task 1"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList()
        )
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.updateTask(task) } throws exception

        // WHEN
        val result = useCase.execute(task)

        // THEN
        assertIs<EditTaskUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
