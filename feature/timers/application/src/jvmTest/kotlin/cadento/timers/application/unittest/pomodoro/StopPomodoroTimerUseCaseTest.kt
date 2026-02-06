package cadento.timers.application.unittest.pomodoro

import cadento.timers.application.TimerRepository
import cadento.timers.application.pomodoro.StopPomodoroTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StopPomodoroTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = StopPomodoroTimerUseCase(repository, clock)

    @Test
    fun `execute with focus timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Focus(now.minus(kotlin.time.Duration.parse("1h")), now))
        
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with preparation timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Preparation(now.minus(kotlin.time.Duration.parse("1h")), now))
        
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with already paused timer returns Success without change`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Paused(now))
        
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute when timer not found returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with get failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("DB Error")
        coEvery { repository.getPomodoroTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Focus(now.minus(kotlin.time.Duration.parse("1h")), now))
        val exception = RuntimeException("Update Error")

        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopPomodoroTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    private fun createTimer(id: TimerId, state: PomodoroTimerState): PomodoroTimer {
        return PomodoroTimer(
            id = id,
            name = TimerName.createOrThrow("Work"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            state = state,
            linkedTask = null,
            settings = PomodoroTimerSettings()
        )
    }
}
