package cadento.timers.application.unittest

import cadento.timers.application.LinkTaskToTimerUseCase
import cadento.timers.application.TimerRepository
import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class LinkTaskToTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val useCase = LinkTaskToTimerUseCase(repository)

    @Test
    fun `execute with existing timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val task: LinkedTimerTask = mockk()
        val timer: Timer = mockk()
        val updatedTimer: Timer = mockk()

        coEvery { repository.getTimer(id) } returns Result.success(timer)
        coEvery { timer.linkTask(task) } returns updatedTimer
        coEvery { repository.updateTimer(updatedTimer) } returns Result.success(updatedTimer)

        // WHEN
        val result = useCase.execute(id, task)

        // THEN
        assertIs<LinkTaskToTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val task: LinkedTimerTask = mockk()
        coEvery { repository.getTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id, task)

        // THEN
        assertIs<LinkTaskToTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with get failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val task: LinkedTimerTask = mockk()
        val exception = RuntimeException("Get Error")
        coEvery { repository.getTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id, task)

        // THEN
        assertIs<LinkTaskToTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val task: LinkedTimerTask = mockk()
        val timer: Timer = mockk()
        val updatedTimer: Timer = mockk()
        val exception = RuntimeException("Update Error")

        coEvery { repository.getTimer(id) } returns Result.success(timer)
        coEvery { timer.linkTask(task) } returns updatedTimer
        coEvery { repository.updateTimer(updatedTimer) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id, task)

        // THEN
        assertIs<LinkTaskToTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
