package timemate.tasks.application.unittest

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
import timemate.tasks.application.GetSoonDueTasksUseCase
import timemate.tasks.application.TaskRepository
import timemate.tasks.application.TimeZoneProvider
import timemate.tasks.domain.Task
import timemate.tasks.domain.TaskDescription
import timemate.tasks.domain.TaskId
import timemate.tasks.domain.TaskName
import timemate.tasks.domain.TaskStatus
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
    private val useCase = GetSoonDueTasksUseCase(
        taskRepository = taskRepository,
        timeZoneProvider = timeZoneProvider,
        clock = clock,
    )

    @Test
    fun `execute returns Success with grouped tasks`() = runTest {
        // GIVEN fixed time, UTC zone and tasks for different ranges
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        val dueTask = createTask(Uuid.random(), now - 1.hours)
        val todayTask = createTask(Uuid.random(), now + 2.hours)
        val nextDayTask = createTask(Uuid.random(), now + 1.days)
        val laterInWeekTask = createTask(Uuid.random(), now + 3.days)
        val nextWeekTask = createTask(Uuid.random(), now + 8.days)

        every { taskRepository.observeDueTasks(now) } returns flowOf(listOf(dueTask))
        every {
            taskRepository.observeTasksDueBetween(
                now..Instant.parse("2024-01-01T23:59:59.999999999Z"),
            )
        } returns flowOf(listOf(todayTask))
        every {
            taskRepository.observeTasksDueBetween(
                Instant.parse("2024-01-02T00:00:00Z")..Instant.parse("2024-01-02T23:59:59.999999999Z"),
            )
        } returns flowOf(listOf(nextDayTask))
        every {
            taskRepository.observeTasksDueBetween(
                Instant.parse("2024-01-03T00:00:00Z")..Instant.parse("2024-01-07T23:59:59.999999999Z"),
            )
        } returns flowOf(listOf(laterInWeekTask))
        every {
            taskRepository.observeTasksDueBetween(
                Instant.parse("2024-01-08T00:00:00Z")..Instant.parse("2024-01-14T23:59:59.999999999Z"),
            )
        } returns flowOf(listOf(nextWeekTask))

        // WHEN & THEN we collect results
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
            assertEquals(
                expected = listOf(dueTask),
                actual = result.dueTasks,
            )
            assertEquals(
                expected = listOf(todayTask),
                actual = result.tasksToday,
            )
            assertEquals(
                expected = listOf(nextDayTask),
                actual = result.tasksNextDay,
            )
            assertEquals(
                expected = listOf(laterInWeekTask),
                actual = result.tasksLaterInWeek,
            )
            assertEquals(
                expected = listOf(nextWeekTask),
                actual = result.tasksNextWeek,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute with repository failure returns Error`() = runTest {
        // GIVEN repository that throws exception
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val exceptionMessage = "Database error"
        val exception = RuntimeException(exceptionMessage)
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)
        every { taskRepository.observeDueTasks(any()) } returns flow { throw exception }
        every { taskRepository.observeTasksDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN we collect results
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Error>(result)
            assertIs<RuntimeException>(result.error)
            assertEquals(
                expected = exceptionMessage,
                actual = result.error.message,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute re-triggers emission when task becomes due`() = runTest {
        // GIVEN task due in 1 hour
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val dueTask = createTask(Uuid.random(), now + 1.hours)
        every { clock.now() } returns now andThen (now + 1.hours + 1.days)
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.observeDueTasks(any()) } returns flowOf(listOf(dueTask))
        every { taskRepository.observeTasksDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN we collect results
        useCase.execute().test {
            // Initial emission
            val firstResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(firstResult)
            assertEquals(
                expected = 1,
                actual = firstResult.dueTasks.size,
            )

            // Second emission triggered by internal timer
            val secondResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(secondResult)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute defaults to 1 day delay when no tasks are present`() = runTest {
        // GIVEN no tasks
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.observeDueTasks(any()) } returns flowOf(emptyList())
        every { taskRepository.observeTasksDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN we collect results
        useCase.execute().test {
            // 1. Await the first emission (empty state)
            val firstResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(firstResult)
            assertTrue(firstResult.dueTasks.isEmpty())

            // 2. Advance by 1 day
            advanceTimeBy(1.days.inWholeMilliseconds)

            // 3. Assert no new item yet
            expectNoEvents()

            // 4. Advance past threshold
            advanceTimeBy(200.milliseconds)

            // 5. New item emitted
            val secondResult = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(secondResult)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute correctly handles Saturday for next week calculation`() = runTest {
        // GIVEN Saturday time and task for Monday
        val now = Instant.parse("2024-01-06T10:00:00Z")
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        val laterInWeekTask = createTask(Uuid.random(), now + 2.days) // Monday
        every { taskRepository.observeDueTasks(any()) } returns flowOf(emptyList())
        every { taskRepository.observeTasksDueBetween(any()) } answers {
            val range = firstArg<ClosedRange<Instant>>()
            if (laterInWeekTask.dueTime in range) {
                flowOf(listOf(laterInWeekTask))
            } else {
                flowOf(emptyList())
            }
        }

        // WHEN & THEN we collect results
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
            assertTrue(
                actual = result.tasksLaterInWeek.isEmpty(),
                message = "laterInWeek should be empty on Saturday",
            )
            assertEquals(
                expected = 1,
                actual = result.tasksNextWeek.size,
            )
            assertEquals(
                expected = laterInWeekTask,
                actual = result.tasksNextWeek.first(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute with overdue tasks does not busy-loop`() = runTest {
        // GIVEN overdue task
        val now = Instant.parse("2024-01-01T10:00:00Z")
        val overdueTask = createTask(Uuid.random(), now - 1.hours)
        every { clock.now() } returns now
        every { timeZoneProvider.timeZone } returns MutableStateFlow(TimeZone.UTC)

        every { taskRepository.observeDueTasks(any()) } returns flowOf(listOf(overdueTask))
        every { taskRepository.observeTasksDueBetween(any()) } returns flowOf(emptyList())

        // WHEN & THEN we collect results
        useCase.execute().test {
            // Await the first emission
            awaitItem()

            // Advance time significantly
            advanceTimeBy(1.days.inWholeMilliseconds - 100)

            // Assert no new items emitted, preventing busy-loop
            expectNoEvents()

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
            tags = emptyList(),
        )
    }
}
