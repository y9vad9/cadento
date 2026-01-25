package timemate.client.tasks.application.test

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import timemate.client.tasks.application.GetSoonDueTasksUseCase
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.application.TimeZoneProvider
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskStatus
import timemate.client.tasks.domain.TaskTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetSoonDueTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val timeZoneProvider: TimeZoneProvider = mockk()
    private val clock: Clock = mockk()
    private val useCase = GetSoonDueTasksUseCase(taskRepository, timeZoneProvider, clock)

    @Test
    fun `execute returns Success with grouped tasks`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        val dueTask = createTask(Uuid.random(), now - 1.hours)
        val todayTask = createTask(Uuid.random(), now + 2.hours)
        val nextDayTask = createTask(Uuid.random(), now + 1.days)
        val laterInWeekTask = createTask(Uuid.random(), now + 3.days)
        val nextWeekTask = createTask(Uuid.random(), now + 8.days)

        every { taskRepository.getDueTasks(now) } returns flowOf(listOf(dueTask))
        every {
            taskRepository.getTasksWithDueBetween(
                now..Instant.parse("2024-01-01T23:59:59.999999999Z")
            )
        } returns flowOf(listOf(todayTask))
        every {
            taskRepository.getTasksWithDueBetween(
                Instant.parse("2024-01-02T00:00:00Z")..Instant.parse("2024-01-02T23:59:59.999999999Z")
            )
        } returns flowOf(listOf(nextDayTask))
        every {
            taskRepository.getTasksWithDueBetween(
                Instant.parse("2024-01-03T00:00:00Z")..Instant.parse("2024-01-07T23:59:59.999999999Z")
            )
        } returns flowOf(listOf(laterInWeekTask))
        every {
            taskRepository.getTasksWithDueBetween(
                Instant.parse("2024-01-08T00:00:00Z")..Instant.parse("2024-01-14T23:59:59.999999999Z")
            )
        } returns flowOf(listOf(nextWeekTask))

        // WHEN & THEN
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
            assertEquals(listOf(dueTask), result.dueTasks)
            assertEquals(listOf(todayTask), result.tasksToday)
            assertEquals(listOf(nextDayTask), result.tasksNextDay)
            assertEquals(listOf(laterInWeekTask), result.tasksLaterInWeek)
            assertEquals(listOf(nextWeekTask), result.tasksNextWeek)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute returns Error when repository throws exception`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val exceptionMessage = "Database error"
        val exception = RuntimeException(exceptionMessage)
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)
        every { taskRepository.getDueTasks(any()) } returns flow { throw exception }
        every { taskRepository.getTasksWithDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Error>(result)
            assertIs<RuntimeException>(result.error)
            assertEquals(exceptionMessage, result.error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute re-triggers when a task is due`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val dueTask = createTask(Uuid.random(), now + 1.hours) // Due in 1 hour
        every { clock.now() } returns now andThen (now + 1.hours + 1.days)
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.getDueTasks(any()) } returns flowOf(listOf(dueTask))
        every { taskRepository.getTasksWithDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN
        useCase.execute().test {
            // Initial emission
            val firstResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(firstResult)
            assertEquals(1, firstResult.dueTasks.size)

            // Second emission after delay
            val secondResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(secondResult)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute defaults to 1 day delay when no tasks are present`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.getDueTasks(any()) } returns flowOf(emptyList())
        every { taskRepository.getTasksWithDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN
        useCase.execute().test {
            // 1. Await the first emission (empty state)
            val firstResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(firstResult)
            assertTrue(firstResult.dueTasks.isEmpty())
            assertTrue(firstResult.tasksToday.isEmpty())
            assertTrue(firstResult.tasksNextDay.isEmpty())
            assertTrue(firstResult.tasksLaterInWeek.isEmpty())
            assertTrue(firstResult.tasksNextWeek.isEmpty())

            // 2. Manually advance the virtual clock by just under the expected delay.
            // The delay is 1.day + 100ms.
            advanceTimeBy(1.days.inWholeMilliseconds)

            // 3. Assert that no new item has been emitted yet.
            expectNoEvents()

            // 4. Advance the clock past the delay threshold.
            advanceTimeBy(200.milliseconds)

            // 5. Now, a new item should be emitted.
            val secondResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(secondResult)

            // 6. Clean up
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute handles Saturday edge case for next week calculation`() = runTest {
        // GIVEN
        // Saturday, so that dayAfterNext is Monday
        val now = Instant.parse("2024-01-06T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        val laterInWeekTask = createTask(Uuid.random(), now + 2.days) // Monday
        every { taskRepository.getDueTasks(any()) } returns flowOf(emptyList())
        every { taskRepository.getTasksWithDueBetween(any()) } answers {
            val range = firstArg<ClosedRange<Instant>>()
            if (laterInWeekTask.dueTime in range) {
                flowOf(listOf(laterInWeekTask))
            } else {
                flowOf(emptyList())
            }
        }

        // WHEN & THEN
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
            assertEquals(1, result.tasksLaterInWeek.size)
            assertEquals(laterInWeekTask, result.tasksLaterInWeek.first())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute does not busy-loop with overdue tasks`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val overdueTask = createTask(Uuid.random(), now - 1.hours)
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.getDueTasks(any()) } returns flowOf(listOf(overdueTask))
        every { taskRepository.getTasksWithDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN
        useCase.execute().test {
            // Await the first emission
            awaitItem()

            // Advance time by a small amount, less than the default 1-day delay
            advanceTimeBy(1.days.inWholeMilliseconds - 100)

            // Assert that no new item has been emitted, proving there's no busy-loop
            expectNoEvents()

            // Clean up
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTask(uuid: Uuid, dueTime: Instant): Task {
        return Task.createOrThrow(
            id = TaskId(uuid),
            name = TaskName.createOrThrow("Task $uuid"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned,
            tags = emptyList()
        )
    }
}
