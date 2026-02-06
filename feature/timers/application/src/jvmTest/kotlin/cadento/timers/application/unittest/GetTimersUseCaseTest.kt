package cadento.timers.application.unittest

import app.cash.turbine.test
import cadento.timers.application.GetTimersUseCase
import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerRepository
import cadento.timers.application.TimerSort
import cadento.timers.domain.Timer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetTimersUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val useCase = GetTimersUseCase(repository)

    @Test
    fun `execute with successful repository returns Success`() = runTest {
        // GIVEN
        val timers = listOf<Timer>(mockk(), mockk())
        every { repository.observeTimers(any(), any()) } returns flowOf(Result.success(timers))

        // WHEN & THEN
        useCase.execute(TimerFilter(), TimerSort.ByNameAsc).test {
            val item = awaitItem()
            assertIs<GetTimersUseCase.Result.Success>(item)
            assertEquals(timers, item.timers)
            awaitComplete()
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val exception = RuntimeException("Flow error")
        every { repository.observeTimers(any(), any()) } returns flowOf(Result.failure(exception))

        // WHEN & THEN
        useCase.execute().test {
            val item = awaitItem()
            assertIs<GetTimersUseCase.Result.Error>(item)
            assertEquals(exception, item.error)
            awaitComplete()
        }
    }
}
