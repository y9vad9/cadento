package timemate.tasks.application.unittest

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import timemate.tasks.application.DeleteTaskUseCase
import timemate.tasks.application.TaskRepository
import timemate.tasks.domain.TaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class DeleteTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = DeleteTaskUseCase(taskRepository)

    @Test
    fun `execute with existing taskId returns Success`() = runTest {
        // GIVEN existing task id and repository that succeeds
        val taskId = TaskId(Uuid.random())
        coEvery { taskRepository.deleteTask(taskId) } returns Result.success(Unit)

        // WHEN we execute use case
        val result = useCase.execute(taskId)

        // THEN result is Success
        assertIs<DeleteTaskUseCase.Result.Success>(result)
        coVerify { taskRepository.deleteTask(taskId) }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN task id and repository that fails
        val taskId = TaskId(Uuid.random())
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.deleteTask(taskId) } returns Result.failure(exception)

        // WHEN we execute use case
        val result = useCase.execute(taskId)

        // THEN result is error with caught exception
        assertIs<DeleteTaskUseCase.Result.Error>(result)
        assertEquals(
            expected = exception,
            actual = result.error,
        )
    }
}
