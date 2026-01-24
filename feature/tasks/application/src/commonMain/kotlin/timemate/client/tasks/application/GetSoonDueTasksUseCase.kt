package timemate.client.tasks.application

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
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timemate.client.tasks.domain.Task
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
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
                        val currentLocalDateTime = now.toLocalDateTime(timeZone)

                        val ranges = calculateDateRanges(currentLocalDateTime, timeZone)

                        val dueTasksFlow = taskRepository.getDueTasks(now)
                        val todayTasksFlow = taskRepository.getTasksWithDueBetween(ranges.today)
                        val nextDayTasksFlow = taskRepository.getTasksWithDueBetween(ranges.nextDay)
                        val laterInWeekTasksFlow = taskRepository.getTasksWithDueBetween(ranges.laterInWeek)
                        val nextWeekTasksFlow = taskRepository.getTasksWithDueBetween(ranges.nextWeek)

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

    private data class DateRanges(
        val today: ClosedRange<Instant>,
        val nextDay: ClosedRange<Instant>,
        val laterInWeek: ClosedRange<Instant>,
        val nextWeek: ClosedRange<Instant>,
    )

    private fun calculateDateRanges(currentLocalDateTime: LocalDateTime, timeZone: TimeZone): DateRanges {
        val currentDate = currentLocalDateTime.date

        val nextDayDate = currentDate.plus(DatePeriod(days = 1))
        val endOfToday = nextDayDate.atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val todayRange = currentLocalDateTime.toInstant(timeZone)..endOfToday

        val nextDayStart = nextDayDate.atTime(0, 0).toInstant(timeZone)
        val endOfNextDay = nextDayDate.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val nextDayRange = nextDayStart..endOfNextDay

        val dayAfterNext = nextDayDate.plus(DatePeriod(days = 1))
        val endOfWeekDate = dayAfterNext.plus(
            DatePeriod(days = DayOfWeek.SUNDAY.ordinal - dayAfterNext.dayOfWeek.ordinal)
        )
        val laterInWeekStart = dayAfterNext.atTime(0, 0).toInstant(timeZone)
        val laterInWeekEnd = endOfWeekDate.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val laterInWeekRange = laterInWeekStart..laterInWeekEnd

        val nextMonday = endOfWeekDate.plus(DatePeriod(days = 1))
        val nextSunday = nextMonday.plus(DatePeriod(days = 6))
        val nextWeekStart = nextMonday.atTime(0, 0).toInstant(timeZone)
        val nextWeekEnd = nextSunday.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val nextWeekRange = nextWeekStart..nextWeekEnd

        return DateRanges(
            today = todayRange,
            nextDay = nextDayRange,
            laterInWeek = laterInWeekRange,
            nextWeek = nextWeekRange
        )
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
