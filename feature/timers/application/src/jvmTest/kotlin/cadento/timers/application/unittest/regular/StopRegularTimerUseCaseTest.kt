package cadento.timers.application.unittest.regular

import cadento.timers.application.TimerRepository
import cadento.timers.application.regular.StopRegularTimerUseCase
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StopRegularTimerUseCaseTest {
    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = StopRegularTimerUseCase(repository, clock)

    @Test
    fun `execute with active timer returns Success`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = RegularTimer(
            id = id,
            name = TimerName.createOrThrow("Test"),
            creationTime = now,
            state = RegularTimerState.Active(now, null),
            linkedTask = null
        )

        coEvery { repository.getRegularTimer(id) } returns Result.success(timer)
        coEvery { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopRegularTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with already inactive timer returns Success without change`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = RegularTimer(
            id = id,
            name = TimerName.createOrThrow("Test"),
            creationTime = now,
            state = RegularTimerState.Inactive(now, null),
            linkedTask = null
        )

        coEvery { repository.getRegularTimer(id) } returns Result.success(timer)
        coEvery { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.success(timer)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopRegularTimerUseCase.Result.Success>(result)
    }

    @Test
    fun `execute with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        coEvery { repository.getRegularTimer(id) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopRegularTimerUseCase.Result.TimerNotFound>(result)
    }

    @Test
    fun `execute with get failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val exception = RuntimeException("DB Error")
        coEvery { repository.getRegularTimer(id) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopRegularTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }

    @Test
    fun `execute with update failure returns Error`() = runTest {
        // GIVEN
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val timer = RegularTimer(
            id = id,
            name = TimerName.createOrThrow("Test"),
            creationTime = now,
            state = RegularTimerState.Active(now, null),
            linkedTask = null
        )
        val exception = RuntimeException("Update Error")

        coEvery { repository.getRegularTimer(id) } returns Result.success(timer)
        coEvery { clock.now() } returns now
        coEvery { repository.updateTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(id)

        // THEN
        assertIs<StopRegularTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
