package cadento.timers.application.unittest

import cadento.timers.application.TimerRepository
import cadento.timers.application.UnlinkTaskFromTimerUseCase
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class UnlinkTaskFromTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val useCase = UnlinkTaskFromTimerUseCase(repository)

    @Test
    fun `execute with existing timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val timer: Timer = mockk()
        val updatedTimer: Timer = mockk()

        coEvery { repository.getTimer(id) } returns Result.success(timer)
        coEvery { timer.unlinkTask() } returns updatedTimer
        coEvery { repository.updateTimer(updatedTimer) } returns Result.success(updatedTimer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<UnlinkTaskFromTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.getTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<UnlinkTaskFromTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with get failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("Get Error")
        coEvery { repository.getTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<UnlinkTaskFromTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val timer: Timer = mockk()
        val updatedTimer: Timer = mockk()
        val exception = RuntimeException("Update Error")

        coEvery { repository.getTimer(id) } returns Result.success(timer)
        coEvery { timer.unlinkTask() } returns updatedTimer
        coEvery { repository.updateTimer(updatedTimer) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<UnlinkTaskFromTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with domain error returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val timer: Timer = mockk()
        val exception = IllegalArgumentException("Domain Error")

        coEvery { repository.getTimer(id) } returns Result.success(timer)
        coEvery { timer.unlinkTask() } throws exception

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<UnlinkTaskFromTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
