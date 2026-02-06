package cadento.timers.application.unittest.pomodoro

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.application.pomodoro.CreatePomodoroTimerUseCase
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

class CreatePomodoroTimerUseCaseTest {

    private val timerRepository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val idProvider: TimerIdProvider = mockk()
    private val useCase = CreatePomodoroTimerUseCase(timerRepository, clock, idProvider)

    @Test
    fun `execute with valid data returns Success`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Focus")
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val settings = PomodoroTimerSettings()
        
        every { idProvider.nextId() } returns id
        every { clock.now() } returns now
        coEvery { timerRepository.createTimer(any()) } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(name, settings)

        // THEN
        assertIs<CreatePomodoroTimerUseCase.Result.Success>(result)
        val timer = result.timer
        assertEquals(id, timer.id)
        assertEquals(name, timer.name)
        assertEquals(now, timer.creationTime)
        assertIs<PomodoroTimerState.Inactive>(timer.state)
        
        coVerify { 
            timerRepository.createTimer(match { 
                it is PomodoroTimer && it.id == id && it.name == name 
            }) 
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Focus")
        val exception = RuntimeException("DB Error")
        
        every { idProvider.nextId() } returns TimerId(Uuid.random())
        every { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")
        coEvery { timerRepository.createTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(name)

        // THEN
        assertIs<CreatePomodoroTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
