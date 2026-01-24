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

class DeleteTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val useCase = DeleteTasksUseCase(taskRepository)

    @Test
    fun `execute deletes tasks and returns Success`() = runTest {
        // GIVEN
        val taskIds = listOf(TaskId.createOrThrow(1L), TaskId.createOrThrow(2L))
        coEvery { taskRepository.deleteTasks(taskIds) } returns Unit

        // WHEN
        val result = useCase.execute(taskIds)

        // THEN
        assertIs<DeleteTasksUseCase.Result.Success>(result)
        assertEquals(taskIds, result.taskIds)
        coVerify { taskRepository.deleteTasks(taskIds) }
    }

    @Test
    fun `execute returns Error when repository fails`() = runTest {
        // GIVEN
        val taskIds = listOf(TaskId.createOrThrow(1L), TaskId.createOrThrow(2L))
        val exception = RuntimeException("DB Error")
        coEvery { taskRepository.deleteTasks(taskIds) } throws exception

        // WHEN
        val result = useCase.execute(taskIds)

        // THEN
        assertIs<DeleteTasksUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
