package cadento.timers.application.unittest.pomodoro

import cadento.timers.application.TimerRepository
import cadento.timers.application.pomodoro.StartPomodoroTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StartPomodoroTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = StartPomodoroTimerUseCase(repository, clock)

    @Test
    fun `execute with inactive timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Inactive(now.minus(kotlin.time.Duration.parse("1h"))))
        
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StartPomodoroTimerUseCase.Result.Success>(result)
        coVerify { 
            repository.updateTimer(match { 
                it is PomodoroTimer && it.state is PomodoroTimerState.Focus 
            }) 
        }
    }

    @Test
    fun `execute with paused timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Paused(now.minus(kotlin.time.Duration.parse("1h"))))
        
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StartPomodoroTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with already running timer returns Success without change`() = runTest {
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
        assertIs<StartPomodoroTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute when timer not found returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.getPomodoroTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StartPomodoroTimerUseCase.Result.TimerNotFound>(result)
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
        assertIs<StartPomodoroTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, PomodoroTimerState.Inactive(now.minus(kotlin.time.Duration.parse("1h"))))
        val exception = RuntimeException("Update Error")

        coEvery { repository.getPomodoroTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StartPomodoroTimerUseCase.Result.Error>(result)
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
