package cadento.timers.infrastructure.unittest

import app.cash.turbine.test
import cadento.timers.application.DeleteTimerResult
import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerRepository
import cadento.timers.application.TimerSort
import cadento.timers.database.DbTimer
import cadento.timers.database.DbTimerSort
import cadento.timers.database.TimersDatabaseSource
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.infrastructure.DbTimerMapper
import cadento.timers.infrastructure.TimerRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class TimerRepositoryImplTest {

    private val databaseSource: TimersDatabaseSource = mockk()
    private val dbTimerMapper: DbTimerMapper = mockk()
    private val repository: TimerRepository = TimerRepositoryImpl(
        databaseSource = databaseSource,
        dbTimerMapper = dbTimerMapper,
    )

    @Test
    fun `createTimer with valid timer returns Success`() = runTest {
        // GIVEN a valid domain timer and its database representation
        val timer = createMockTimer()
        val dbTimer = mockk<DbTimer>()
        every { dbTimerMapper.mapToDb(timer) } returns dbTimer
        coEvery { databaseSource.insertTimer(dbTimer) } returns Unit

        // WHEN we create the timer
        val result = repository.createTimer(timer)

        // THEN the operation is successful and the database source is called
        assertTrue(result.isSuccess)
        coVerify { databaseSource.insertTimer(dbTimer) }
    }

    @Test
    fun `createTimer with repository failure returns Failure`() = runTest {
        // GIVEN a timer and a failing database source
        val timer = createMockTimer()
        val exception = RuntimeException("DB Error")
        every { dbTimerMapper.mapToDb(timer) } returns mockk()
        coEvery { databaseSource.insertTimer(any()) } throws exception

        // WHEN we attempt to create the timer
        val result = repository.createTimer(timer)

        // THEN we receive a failure result with the underlying exception
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `updateTimer with existing timer returns Success`() = runTest {
        // GIVEN an existing timer and its database representation
        val timer = createMockTimer()
        val dbTimer = mockk<DbTimer>()
        every { dbTimerMapper.mapToDb(timer) } returns dbTimer
        coEvery { databaseSource.updateTimer(dbTimer) } returns dbTimer
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we update the timer
        val result = repository.updateTimer(timer)

        // THEN the operation is successful and returns the updated timer
        assertTrue(result.isSuccess)
        assertEquals(timer, result.getOrNull())
    }

    @Test
    fun `updateTimer with repository failure returns Failure`() = runTest {
        // GIVEN a timer and an update failure in the source
        val timer = createMockTimer()
        val exception = RuntimeException("Update Error")
        every { dbTimerMapper.mapToDb(timer) } returns mockk()
        coEvery { databaseSource.updateTimer(any()) } throws exception

        // WHEN we attempt to update the timer
        val result = repository.updateTimer(timer)

        // THEN we receive a failure result
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteTimer with existing timer returns Success`() = runTest {
        // GIVEN a timer ID that exists in the database
        val id = TimerId(Uuid.random())
        coEvery { databaseSource.deleteTimer(id.value) } returns true

        // WHEN we delete the timer
        val result = repository.deleteTimer(id)

        // THEN we receive a Success result and the source is notified
        assertEquals(Result.success(DeleteTimerResult.Success), result)
        coVerify { databaseSource.deleteTimer(id.value) }
    }

    @Test
    fun `deleteTimer with non existing timer returns TimerNotFound`() = runTest {
        // GIVEN a timer ID that does not exist
        val id = TimerId(Uuid.random())
        coEvery { databaseSource.deleteTimer(id.value) } returns false

        // WHEN we attempt to delete the timer
        val result = repository.deleteTimer(id)

        // THEN we receive a TimerNotFound result
        assertEquals(Result.success(DeleteTimerResult.TimerNotFound), result)
    }

    @Test
    fun `observeTimers with successful source returns Success`() = runTest {
        // GIVEN a filter, sort preference, and a list of database timers
        val filter = TimerFilter(nameContains = "work")
        val sort = TimerSort.ByNameAsc
        val dbTimer = mockk<DbTimer>()
        val timer = createMockTimer()
        
        every { dbTimerMapper.mapSort(sort) } returns DbTimerSort.BY_NAME_ASC
        every { databaseSource.observeTimers("work", "BY_NAME_ASC") } returns flowOf(listOf(dbTimer))
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we observe the timers
        val flow = repository.observeTimers(filter, sort)

        // THEN we receive a Success result containing the mapped domain timers
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(listOf(timer), result.getOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTimers with mapping failure returns Failure`() = runTest {
        // GIVEN a database source that returns timers but a failing mapper
        val filter = TimerFilter()
        val sort = TimerSort.ByCreationTimeDesc
        val exception = RuntimeException("Mapping Error")
        
        every { dbTimerMapper.mapSort(sort) } returns DbTimerSort.BY_CREATION_DESC
        val dbTimer = mockk<DbTimer>()
        every { databaseSource.observeTimers(any(), any()) } returns flowOf(listOf(dbTimer))
        every { dbTimerMapper.mapToDomain(dbTimer) } throws exception

        // WHEN we observe the timers
        val flow = repository.observeTimers(filter, sort)

        // THEN we receive a failure result from the flow
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observePomodoroTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Pomodoro timer in the database
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<PomodoroTimer>()
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we observe the Pomodoro timer
        val flow = repository.observePomodoroTimer(id)

        // THEN we receive the corresponding domain timer
        flow.test {
            val result = awaitItem()
            assertEquals(timer, result.getOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observePomodoroTimer with mapping failure returns Failure`() = runTest {
        // GIVEN a database timer but a failing mapper
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val exception = RuntimeException("Map Error")
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } throws exception

        // WHEN we observe the Pomodoro timer
        val flow = repository.observePomodoroTimer(id)

        // THEN we receive a failure result
        flow.test {
            assertTrue(awaitItem().isFailure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeRegularTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Regular timer in the database
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<RegularTimer>()
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we observe the Regular timer
        val flow = repository.observeRegularTimer(id)

        // THEN we receive the domain timer
        flow.test {
            val result = awaitItem()
            assertEquals(timer, result.getOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeRegularTimer with mapping failure returns Failure`() = runTest {
        // GIVEN a database timer and a failing mapper
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val exception = RuntimeException("Map Error")
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } throws exception

        // WHEN we observe the Regular timer
        val flow = repository.observeRegularTimer(id)

        // THEN we receive a failure result
        flow.test {
            assertTrue(awaitItem().isFailure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeFocusDividendTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Focus Dividend timer in the database
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<FocusDividendTimer>()
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we observe the Focus Dividend timer
        val flow = repository.observeFocusDividendTimer(id)

        // THEN we receive the corresponding domain model
        flow.test {
            val result = awaitItem()
            assertEquals(timer, result.getOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeFocusDividendTimer with mapping failure returns Failure`() = runTest {
        // GIVEN a timer in the database but a failing domain mapper
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val exception = RuntimeException("Map Error")
        every { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } throws exception

        // WHEN we observe the Focus Dividend timer
        val flow = repository.observeFocusDividendTimer(id)

        // THEN we receive a failure result
        flow.test {
            assertTrue(awaitItem().isFailure)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getPomodoroTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Pomodoro timer
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<PomodoroTimer>()
        coEvery { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we fetch the Pomodoro timer
        val result = repository.getPomodoroTimer(id)

        // THEN we receive a Success result with the timer
        assertTrue(result.isSuccess)
        assertEquals(timer, result.getOrNull())
    }

    @Test
    fun `getPomodoroTimer with non existing id returns Success with null`() = runTest {
        // GIVEN a timer ID that does not exist in the source
        val id = TimerId(Uuid.random())
        coEvery { databaseSource.observeTimer(id.value) } returns flowOf(null)

        // WHEN we attempt to fetch the timer
        val result = repository.getPomodoroTimer(id)

        // THEN we receive a Success result containing null
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `getRegularTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Regular timer
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<RegularTimer>()
        coEvery { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we fetch the Regular timer
        val result = repository.getRegularTimer(id)

        // THEN the operation is successful
        assertTrue(result.isSuccess)
        assertEquals(timer, result.getOrNull())
    }

    @Test
    fun `getFocusDividendTimer with existing id returns Success`() = runTest {
        // GIVEN an existing Focus Dividend timer
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<FocusDividendTimer>()
        coEvery { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we fetch the Focus Dividend timer
        val result = repository.getFocusDividendTimer(id)

        // THEN we receive the timer domain model
        assertTrue(result.isSuccess)
        assertEquals(timer, result.getOrNull())
    }

    @Test
    fun `getTimer with existing id returns Success`() = runTest {
        // GIVEN an existing timer of any type
        val id = TimerId(Uuid.random())
        val dbTimer = mockk<DbTimer>()
        val timer = mockk<Timer>()
        coEvery { databaseSource.observeTimer(id.value) } returns flowOf(dbTimer)
        every { dbTimerMapper.mapToDomain(dbTimer) } returns timer

        // WHEN we fetch the general timer
        val result = repository.getTimer(id)

        // THEN we receive the timer in a Success result
        assertTrue(result.isSuccess)
        assertEquals(timer, result.getOrNull())
    }

    // Helpers

    private fun createMockTimer(id: TimerId = TimerId(Uuid.random())): Timer {
        val timer = mockk<Timer>()
        every { timer.id } returns id
        return timer
    }
}
