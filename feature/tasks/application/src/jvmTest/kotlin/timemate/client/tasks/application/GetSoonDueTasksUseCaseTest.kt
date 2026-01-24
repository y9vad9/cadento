package timemate.client.tasks.application

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
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

class GetSoonDueTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val clock: Clock = mockk()
    private val useCase = GetSoonDueTasksUseCase(taskRepository, clock)

    @Test
    fun `execute returns Success with grouped tasks`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now

        val dueTask = createTask("1", now - 1.hours)
        val todayTask = createTask("2", now + 2.hours)
        val nextDayTask = createTask("3", now + 1.days)
        val thisWeekTask = createTask("4", now + 3.days)
        val nextWeekTask = createTask("5", now + 8.days)

        every { taskRepository.getDueTasks(any()) } returns flowOf(listOf(dueTask))
        every { taskRepository.getTasksWithDueBetween(any()) } answers {
            val range = firstArg<ClosedRange<Instant>>()
            when {
                todayTask.dueTime in range -> flowOf(listOf(todayTask))
                nextDayTask.dueTime in range -> flowOf(listOf(nextDayTask))
                thisWeekTask.dueTime in range -> flowOf(listOf(thisWeekTask))
                nextWeekTask.dueTime in range -> flowOf(listOf(nextWeekTask))
                else -> flowOf(emptyList())
            }
        }

        // WHEN & THEN
        useCase.execute().test {
            val result = awaitItem()
            assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
            assertEquals(listOf(dueTask), result.dueTasks)
            assertEquals(listOf(todayTask), result.tasksToday)
            assertEquals(listOf(nextDayTask), result.tasksNextDay)
            assertEquals(listOf(thisWeekTask), result.tasksThisWeek)
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
        val dueTask = createTask("1", now + 1.hours) // Due in 1 hour
        every { clock.now() } returns now andThen (now + 1.hours + 1.days)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `execute defaults to 1 day delay when no tasks are present`() = runTest {
        // GIVEN
        val now = Instant.parse("2024-01-01T10:00:00Z")
        every { clock.now() } returns now

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
            assertTrue(firstResult.tasksThisWeek.isEmpty())
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

    private fun createTask(id: String, dueTime: Instant): Task {
        return Task.createOrThrow(
            id = TaskId.createOrThrow(id.toLong()),
            name = TaskName.createOrThrow("Task $id"),
            description = TaskDescription.createOrThrow("Description"),
            creationTime = Instant.parse("2024-01-01T00:00:00Z"),
            dueTime = dueTime,
            status = TaskStatus.Builtin.Planned,
            tags = emptyList()
        )
    }
}
