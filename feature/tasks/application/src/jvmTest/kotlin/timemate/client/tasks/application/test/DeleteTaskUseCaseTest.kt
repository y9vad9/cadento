package timemate.client.tasks.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.DeleteTaskUseCase
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.domain.TaskId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class DeleteTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = DeleteTaskUseCase(taskRepository)

    @Test
    fun `execute deletes task and returns Success`() = runTest {
        // GIVEN
        val taskId = TaskId(Uuid.random())
        coEvery { taskRepository.deleteTask(taskId) } returns Unit

        // WHEN
        val result = useCase.execute(taskId)

        // THEN
        assertIs<DeleteTaskUseCase.Result.Success>(result)
        assertEquals(taskId, result.taskId)
        coVerify { taskRepository.deleteTask(taskId) }
    }

    @Test
    fun `execute returns Error when repository fails`() = runTest {
        // GIVEN
        val taskId = TaskId(Uuid.random())
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.deleteTask(taskId) } throws exception

        // WHEN
        val result = useCase.execute(taskId)

        // THEN
        assertIs<DeleteTaskUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
