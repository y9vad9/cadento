package cadento.timers.application.unittest.regular

import cadento.timers.application.TimerIdProvider
import cadento.timers.application.TimerRepository
import cadento.timers.application.regular.CreateRegularTimerUseCase
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
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

class CreateRegularTimerUseCaseTest {

    private val repository: TimerRepository = mockk()
    private val clock: Clock = mockk()
    private val idProvider: TimerIdProvider = mockk()
    private val useCase = CreateRegularTimerUseCase(repository, clock, idProvider)

    @Test
    fun `execute with valid name returns Success`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Gym")
        val id = TimerId(Uuid.random())
        val now = Instant.parse("2024-01-01T00:00:00Z")
        
        every { idProvider.nextId() } returns id
        every { clock.now() } returns now
        coEvery { repository.createTimer(any()) } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(name)

        // THEN
        assertIs<CreateRegularTimerUseCase.Result.Success>(result)
        val timer = result.timer
        assertEquals(id, timer.id)
        assertIs<RegularTimerState.Inactive>(timer.state)
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN
        val name = TimerName.createOrThrow("Gym")
        val exception = RuntimeException("DB Error")
        
        every { idProvider.nextId() } returns TimerId(Uuid.random())
        every { clock.now() } returns Instant.parse("2024-01-01T00:00:00Z")
        coEvery { repository.createTimer(any()) } returns Result.failure(exception)

        // WHEN
        val result = useCase.execute(name)

        // THEN
        assertIs<CreateRegularTimerUseCase.Result.Error>(result)
        assertEquals(exception, result.error)
    }
}
