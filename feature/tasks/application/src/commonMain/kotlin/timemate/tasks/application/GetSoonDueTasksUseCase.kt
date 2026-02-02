package timemate.tasks.application

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.isActive
import timemate.tasks.domain.Task
import timemate.tasks.domain.TaskDuePolicy
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class GetSoonDueTasksUseCase(
    private val taskRepository: TaskRepository,
    private val timeZoneProvider: TimeZoneProvider,
    private val clock: Clock,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(): Flow<Result> {
        val forceUpdates: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
        forceUpdates.tryEmit(Unit)

        return timeZoneProvider.timeZone.flatMapLatest { timeZone ->
            channelFlow<Result> {
                while (isActive) {
                    forceUpdates.collectLatest {
                        val now = clock.now()
                        val ranges = TaskDuePolicy.calculateDateRanges(now, timeZone)

                        val dueTasksFlow = taskRepository.observeDueTasks(now)
                        val todayTasksFlow = taskRepository.observeTasksDueBetween(ranges.today)
                        val nextDayTasksFlow = taskRepository.observeTasksDueBetween(ranges.nextDay)
                        val laterInWeekTasksFlow = taskRepository.observeTasksDueBetween(ranges.laterInWeek)
                        val nextWeekTasksFlow = taskRepository.observeTasksDueBetween(ranges.nextWeek)

                        combine(
                            dueTasksFlow,
                            todayTasksFlow,
                            nextDayTasksFlow,
                            laterInWeekTasksFlow,
                            nextWeekTasksFlow
                        ) { dueTasks, todayTasks, nextDayTasks, laterInWeekTasks, nextWeekTasks ->
                            Result.Success(
                                dueTasks = dueTasks,
                                tasksToday = todayTasks,
                                tasksNextDay = nextDayTasks,
                                tasksLaterInWeek = laterInWeekTasks,
                                tasksNextWeek = nextWeekTasks
                            )
                        }.collectLatest { result ->
                            send(result)

                            val nextTrigger = calculateNextTrigger(result, now)
                            delay(nextTrigger)
                            forceUpdates.emit(Unit)
                        }
                    }
                }
            }.catch { emit(Result.Error(it)) }
        }
    }

    private fun calculateNextTrigger(result: Result.Success, now: Instant): Duration {
        val allTasks = listOf(
            result.dueTasks,
            result.tasksToday,
            result.tasksNextDay,
            result.tasksLaterInWeek,
            result.tasksNextWeek
        )
        val nextTriggerTime = allTasks
            .flatten()
            .filter { it.dueTime > now } // Only consider future tasks
            .minOfOrNull { it.dueTime } ?: now.plus(1.days)

        return (nextTriggerTime - now).coerceAtLeast(1.seconds) + 100.milliseconds
    }

    sealed interface Result {
        data class Error(val error: Throwable) : Result
        data class Success(
            val dueTasks: List<Task>,
            val tasksToday: List<Task>,
            val tasksNextDay: List<Task>,
            val tasksLaterInWeek: List<Task>,
            val tasksNextWeek: List<Task>,
        ) : Result
    }
}
