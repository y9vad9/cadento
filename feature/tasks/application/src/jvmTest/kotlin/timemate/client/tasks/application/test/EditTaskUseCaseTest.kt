package timemate.client.tasks.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
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
import kotlin.uuid.Uuid

class EditTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = EditTaskUseCase(taskRepository)

    @Test
    fun `execute updates task and returns Success`() = runTest {
        // GIVEN
        val originalTask = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Original Name"),
            description = TaskDescription.createOrThrow("Original Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList()
        )
        val newName = TaskName.createOrThrow("New Name")
        val patch = EditTaskUseCase.TaskPatch(newName = newName)
        val updatedTask = originalTask.rename(newName)

        coEvery { taskRepository.getTask(originalTask.id) } returns flowOf(originalTask)
        coEvery { taskRepository.updateTask(updatedTask) } returns Unit

        // WHEN
        val result = useCase.execute(taskId = originalTask.id, patch = patch)

        // THEN
        assertIs<EditTaskUseCase.Result.Success>(result)
        assertEquals(updatedTask, result.task)
        coVerify { taskRepository.updateTask(updatedTask) }
    }

    @Test
    fun `execute returns TaskNotFound when task does not exist`() = runTest {
        // GIVEN
        val taskId = TaskId(Uuid.random())
        val patch = EditTaskUseCase.TaskPatch()
        coEvery { taskRepository.getTask(taskId) } returns flowOf(null)

        // WHEN
        val result = useCase.execute(taskId = taskId, patch = patch)

        // THEN
        assertIs<EditTaskUseCase.Result.TaskNotFound>(result)
    }

    @Test
    fun `execute returns InvalidDueTime when new due time is invalid`() = runTest {
        // GIVEN
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task Name"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-02T00:00:00Z"),
            dueTime = Instant.parse("2024-01-03T00:00:00Z"),
            tags = emptyList()
        )
        val invalidDueTime = Instant.parse("2024-01-01T00:00:00Z")
        val patch = EditTaskUseCase.TaskPatch(newDueTime = invalidDueTime)

        coEvery { taskRepository.getTask(task.id) } returns flowOf(task)

        // WHEN
        val result = useCase.execute(taskId = task.id, patch = patch)

        // THEN
        assertIs<EditTaskUseCase.Result.InvalidDueTime>(result)
    }

    @Test
    fun `execute returns Error when repository fails`() = runTest {
        // GIVEN
        val taskId = TaskId(Uuid.random())
        val patch = EditTaskUseCase.TaskPatch()
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.getTask(taskId) } throws exception

        // WHEN
        val result = useCase.execute(taskId = taskId, patch = patch)

        // THEN
        assertIs<EditTaskUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
