package cadento.tasks.application.unittest

import cadento.tasks.application.EditTaskUseCase
import cadento.tasks.application.TaskRepository
import cadento.tasks.domain.Task
import cadento.tasks.domain.TaskDescription
import cadento.tasks.domain.TaskId
import cadento.tasks.domain.TaskName
import cadento.tasks.domain.TaskStatus
import cadento.tasks.domain.TaskTag
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import kotlin.uuid.Uuid

class EditTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = EditTaskUseCase(taskRepository)

    @Test
    fun `execute with existing task and valid patch returns Success`() = runTest {
        // GIVEN existing task, valid patch and repository that succeeds
        val originalTask = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Original Name"),
            description = TaskDescription.createOrThrow("Original Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = emptyList(),
        )
        val newName = TaskName.createOrThrow("New Name")
        val patch = EditTaskUseCase.TaskPatch(newName = newName)
        val updatedTask = originalTask.rename(newName)

        coEvery { taskRepository.getTask(originalTask.id) } returns Result.success(originalTask)
        coEvery { taskRepository.updateTask(updatedTask) } returns Result.success(updatedTask)

        // WHEN we execute use case
        val result = useCase.execute(
            taskId = originalTask.id,
            patch = patch,
        )

        // THEN result is success with updated task
        assertIs<EditTaskUseCase.Result.Success>(result)
        assertEquals(
            expected = updatedTask,
            actual = result.task,
        )
        coVerify { taskRepository.updateTask(updatedTask) }
    }

    @Test
    fun `execute with non-existent taskId returns TaskNotFound`() = runTest {
        // GIVEN non-existent task id
        val taskId = TaskId(Uuid.random())
        val patch = EditTaskUseCase.TaskPatch()
        coEvery { taskRepository.getTask(taskId) } returns Result.success(null)

        // WHEN we execute use case
        val result = useCase.execute(
            taskId = taskId,
            patch = patch,
        )

        // THEN result is TaskNotFound
        assertIs<EditTaskUseCase.Result.TaskNotFound>(result)
    }

    @Test
    fun `execute with invalid due time in patch returns InvalidDueTime`() = runTest {
        // GIVEN task and patch with due time before creation time
        val task = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task Name"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-02T00:00:00Z"),
            dueTime = Instant.parse("2024-01-03T00:00:00Z"),
            tags = emptyList(),
        )
        val invalidDueTime = Instant.parse("2024-01-01T00:00:00Z")
        val patch = EditTaskUseCase.TaskPatch(newDueTime = invalidDueTime)

        coEvery { taskRepository.getTask(task.id) } returns Result.success(task)

        // WHEN we execute use case
        val result = useCase.execute(
            taskId = task.id,
            patch = patch,
        )

        // THEN result is InvalidDueTime
        assertIs<EditTaskUseCase.Result.InvalidDueTime>(result)
    }

    @Test
    fun `execute with new tags in patch returns Success`() = runTest {
        // GIVEN existing task and patch with new tags
        val originalTask = Task.createOrThrow(
            id = TaskId(Uuid.random()),
            name = TaskName.createOrThrow("Task Name"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = Instant.parse("2024-01-02T00:00:00Z"),
            status = TaskStatus.Builtin.Planned,
            tags = listOf(TaskTag.createOrThrow("tag1")),
        )
        val newTags = listOf(TaskTag.createOrThrow("tag2"), TaskTag.createOrThrow("tag3"))
        val patch = EditTaskUseCase.TaskPatch(newTags = newTags)
        val updatedTask = originalTask.updateTags(newTags)

        coEvery { taskRepository.getTask(originalTask.id) } returns Result.success(originalTask)
        coEvery { taskRepository.updateTask(updatedTask) } returns Result.success(updatedTask)

        // WHEN we execute use case
        val result = useCase.execute(
            taskId = originalTask.id,
            patch = patch,
        )

        // THEN result is success with updated tags
        assertIs<EditTaskUseCase.Result.Success>(result)
        assertEquals(
            expected = newTags,
            actual = result.task.tags,
        )
        coVerify { taskRepository.updateTask(updatedTask) }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN task id and repository that fails on get
        val taskId = TaskId(Uuid.random())
        val patch = EditTaskUseCase.TaskPatch()
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.getTask(taskId) } returns Result.failure(exception)

        // WHEN we execute use case
        val result = useCase.execute(
            taskId = taskId,
            patch = patch,
        )

        // THEN result is error with caught exception
        assertIs<EditTaskUseCase.Result.Error>(result)
        assertEquals(
            expected = exception,
            actual = result.error,
        )
    }
}
