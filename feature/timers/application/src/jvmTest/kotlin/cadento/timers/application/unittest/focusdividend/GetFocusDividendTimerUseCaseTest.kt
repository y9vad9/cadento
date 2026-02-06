package cadento.timers.application.unittest.focusdividend

import app.cash.turbine.test
import cadento.timers.application.TimerRepository
import cadento.timers.application.focusdividend.GetFocusDividendTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.FocusDividendTimer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class GetFocusDividendTimerUseCaseTest {
    private val repository: TimerRepository = mockk()
    private val useCase = GetFocusDividendTimerUseCase(repository)

    @Test
    fun `execute with existing timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val timer: FocusDividendTimer = mockk()
        every { repository.observeFocusDividendTimer(id) } returns flowOf(Result.success(timer))

        // WHEN & THEN
        useCase.execute(id).test {
            val item = awaitItem()
            assertIs<GetFocusDividendTimerUseCase.Result.Success>(item)
            assertEquals(timer, item.timer)
            awaitComplete()
        }
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        every { repository.observeFocusDividendTimer(id) } returns flowOf(Result.success(null))

        // WHEN & THEN
        useCase.execute(id).test {
            assertIs<GetFocusDividendTimerUseCase.Result.TimerNotFound>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("DB Error")
        every { repository.observeFocusDividendTimer(id) } returns flowOf(Result.failure(exception))

        // WHEN & THEN
        useCase.execute(id).test {
            val item = awaitItem()
            assertIs<GetFocusDividendTimerUseCase.Result.Error>(item)
            assertEquals(exception, item.error)
            awaitComplete()
        }
    }
}
