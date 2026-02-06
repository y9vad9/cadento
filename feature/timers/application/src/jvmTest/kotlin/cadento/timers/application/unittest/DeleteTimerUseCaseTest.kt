package cadento.timers.application.unittest

import cadento.timers.application.DeleteTimerResult
import cadento.timers.application.DeleteTimerUseCase
import cadento.timers.application.TimerRepository
import cadento.timers.domain.TimerId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class DeleteTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val useCase = DeleteTimerUseCase(repository)

    @Test
    fun `execute with existing timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.deleteTimer(id) } returns Result.success(DeleteTimerResult.Success)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<DeleteTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.deleteTimer(id) } returns Result.success(DeleteTimerResult.TimerNotFound)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<DeleteTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("Delete Error")
        coEvery { repository.deleteTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<DeleteTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
