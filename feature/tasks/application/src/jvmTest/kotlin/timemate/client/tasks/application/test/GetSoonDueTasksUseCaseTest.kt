package timemate.client.tasks.application.test

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import timemate.client.tasks.application.GetSoonDueTasksUseCase
import timemate.client.tasks.application.TaskRepository
import timemate.client.tasks.domain.Task
import timemate.client.tasks.domain.TaskId
import timemate.client.tasks.domain.TaskName
import timemate.client.tasks.domain.TaskDescription
import timemate.client.tasks.domain.TaskStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
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

        // WHEN
        val result = useCase.execute().first()

        // THEN
        assertIs<GetSoonDueTasksUseCase.Result.Success>(result)
        assertEquals(listOf(dueTask), result.dueTasks)
        assertEquals(listOf(todayTask), result.tasksToday)
        assertEquals(listOf(nextDayTask), result.tasksNextDay)
        assertEquals(listOf(thisWeekTask), result.tasksThisWeek)
        assertEquals(listOf(nextWeekTask), result.tasksNextWeek)
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
