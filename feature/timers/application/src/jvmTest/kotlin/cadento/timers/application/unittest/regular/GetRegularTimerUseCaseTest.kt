package cadento.timers.application.unittest.regular

import app.cash.turbine.test
import cadento.timers.application.TimerRepository
import cadento.timers.application.regular.GetRegularTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.RegularTimer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class GetRegularTimerUseCaseTest {
    private val repository: TimerRepository = mockk()
    private val useCase = GetRegularTimerUseCase(repository)

    @Test
    fun `execute with existing timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val timer: RegularTimer = mockk()
        every { repository.observeRegularTimer(id) } returns flowOf(Result.success(timer))

        // WHEN & THEN
        useCase.execute(id).test {
            val item = awaitItem()
            assertIs<GetRegularTimerUseCase.Result.Success>(item)
            assertEquals(timer, item.timer)
            awaitComplete()
        }
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        every { repository.observeRegularTimer(id) } returns flowOf(Result.success(null))

        // WHEN & THEN
        useCase.execute(id).test {
            assertIs<GetRegularTimerUseCase.Result.TimerNotFound>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("DB Error")
        every { repository.observeRegularTimer(id) } returns flowOf(Result.failure(exception))

        // WHEN & THEN
        useCase.execute(id).test {
            val item = awaitItem()
            assertIs<GetRegularTimerUseCase.Result.Error>(item)
            assertEquals(exception, item.error)
            awaitComplete()
        }
    }
}
