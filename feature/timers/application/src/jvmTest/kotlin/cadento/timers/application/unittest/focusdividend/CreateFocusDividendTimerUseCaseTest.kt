package cadento.timers.application.unittest.focusdividend

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.application.focusdividend.CreateFocusDividendTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerState
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

class CreateFocusDividendTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val idProvider: TimerIdProvider = mockk()
    private val useCase = CreateFocusDividendTimerUseCase(repository, clock, idProvider)

    @Test
    fun `execute with valid name returns Success`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Deep Work")
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        
        every { idProvider.nextId() } returns id
        every { clock.now() } returns now
        coEvery { repository.createTimer(any()) } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(name)

        // THEN
        assertIs<CreateFocusDividendTimerUseCase.Result.Success>(result)
        val timer = result.timer
        assertEquals(id, timer.id)
        assertIs<FocusDividendTimerState.Terminated>(timer.state)
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Deep Work")
        val exception = RuntimeException("DB Error")
        
        every { idProvider.nextId() } returns TimerId(Uuid.random())
        every { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")
        coEvery { repository.createTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(name)

        // THEN
        assertIs<CreateFocusDividendTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
