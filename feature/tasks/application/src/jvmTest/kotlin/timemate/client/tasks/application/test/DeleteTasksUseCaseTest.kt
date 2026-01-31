package timemate.client.tasks.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.DeleteTasksUseCase
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.domain.TaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class DeleteTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = DeleteTasksUseCase(taskRepository)

    @Test
    fun `execute with multiple taskIds returns Success`() = runTest {
        // GIVEN list of task ids and repository that succeeds
        val taskIds = listOf(TaskId(Uuid.random()), TaskId(Uuid.random()))
        coEvery { taskRepository.deleteTasks(taskIds) } returns Result.success(Unit)

        // WHEN we execute use case
        val result = useCase.execute(taskIds)

        // THEN result is Success
        assertIs<DeleteTasksUseCase.Result.Success>(result)
        coVerify { taskRepository.deleteTasks(taskIds) }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN list of task ids and repository that fails
        val taskIds = listOf(TaskId(Uuid.random()))
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.deleteTasks(taskIds) } returns Result.failure(exception)

        // WHEN we execute use case
        val result = useCase.execute(taskIds)

        // THEN result is error with caught exception
        assertIs<DeleteTasksUseCase.Result.Error>(result)
        assertEquals(
            expected = exception,
            actual = result.error,
        )
    }
}
