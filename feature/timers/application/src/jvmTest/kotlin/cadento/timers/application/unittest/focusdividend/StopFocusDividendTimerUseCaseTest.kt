package cadento.timers.application.unittest.focusdividend

import cadento.timers.application.TimerRepository
import cadento.timers.application.focusdividend.StopFocusDividendTimerUseCase
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerSettings
import cadento.timers.domain.FocusDividendTimerState
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
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

class StopFocusDividendTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = StopFocusDividendTimerUseCase(repository, clock)

    @Test
    fun `execute from earning state returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, FocusDividendTimerState.Earning(now.minus(kotlin.time.Duration.parse("1h")), null))

        coEvery { repository.getFocusDividendTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute from spending state returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, FocusDividendTimerState.Spending(now.minus(kotlin.time.Duration.parse("1h")), null))

        coEvery { repository.getFocusDividendTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute from terminated state returns Success without update`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, FocusDividendTimerState.Terminated(now, null))

        coEvery { repository.getFocusDividendTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.getFocusDividendTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with get repository failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("DB Error")
        coEvery { repository.getFocusDividendTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update repository failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(id, FocusDividendTimerState.Spending(now, null))
        val exception = RuntimeException("Update Error")

        coEvery { repository.getFocusDividendTimer(id) } returns Result.success(timer)
        every { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopFocusDividendTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    private fun createTimer(id: TimerId, state: FocusDividendTimerState): FocusDividendTimer {
        return FocusDividendTimer(
            id = id,
            name = TimerName.createOrThrow("Focus"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            state = state,
            linkedTask = null,
            settings = FocusDividendTimerSettings()
        )
    }
}
