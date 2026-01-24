package timemate.client.tasks.application

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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
    private val clock: Clock,
) {
    sealed interface Result {
        data class Error(val error: Throwable) : Result
        data class Success(
            val dueTasks: List<Task>,
            val tasksToday: List<Task>,
            val tasksNextDay: List<Task>,
            val tasksThisWeek: List<Task>,
            val tasksNextWeek: List<Task>,
        ) : Result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(): Flow<Result> {
        val forceUpdates: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
        forceUpdates.tryEmit(Unit)

        return channelFlow<Result> {
            while (isActive) {
                forceUpdates.collectLatest {
                    val now = clock.now()
                    val timeZone = TimeZone.UTC
                    val currentLocalDateTime = now.toLocalDateTime(timeZone)

                    val ranges = calculateDateRanges(currentLocalDateTime, timeZone)

                    val dueTasksFlow = taskRepository.getDueTasks(now)
                    val todayTasksFlow = taskRepository.getTasksWithDueBetween(ranges.today)
                    val nextDayTasksFlow = taskRepository.getTasksWithDueBetween(ranges.nextDay)
                    val thisWeekTasksFlow = taskRepository.getTasksWithDueBetween(ranges.thisWeek)
                    val nextWeekTasksFlow = taskRepository.getTasksWithDueBetween(ranges.nextWeek)

                    combine(
                        dueTasksFlow,
                        todayTasksFlow,
                        nextDayTasksFlow,
                        thisWeekTasksFlow,
                        nextWeekTasksFlow
                    ) { dueTasks, todayTasks, nextDayTasks, thisWeekTasks, nextWeekTasks ->
                        Result.Success(
                            dueTasks = dueTasks,
                            tasksToday = todayTasks,
                            tasksNextDay = nextDayTasks,
                            tasksThisWeek = thisWeekTasks,
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

    private fun calculateNextTrigger(result: Result.Success, now: Instant): Duration {
        val allTasks = listOf(
            result.dueTasks,
            result.tasksToday,
            result.tasksNextDay,
            result.tasksThisWeek,
            result.tasksNextWeek
        )
        val nextTriggerTime = allTasks
            .flatten()
            .minOfOrNull { it.dueTime } ?: now.plus(1.days)

        return (nextTriggerTime - now).coerceAtLeast(1.seconds) + 100.milliseconds
    }

    private data class DateRanges(
        val today: ClosedRange<Instant>,
        val nextDay: ClosedRange<Instant>,
        val thisWeek: ClosedRange<Instant>,
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
        val endOfWeekDate = dayAfterNext.plus(DatePeriod(days = DayOfWeek.SUNDAY.ordinal - dayAfterNext.dayOfWeek.ordinal))
        val thisWeekStart = dayAfterNext.atTime(0, 0).toInstant(timeZone)
        val thisWeekEnd = endOfWeekDate.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val thisWeekRange = thisWeekStart..thisWeekEnd

        @Suppress("detekt.MagicNumber")
        val daysUntilNextMonday = (DayOfWeek.MONDAY.ordinal - dayAfterNext.dayOfWeek.ordinal + 7) % 7
        val nextMonday = dayAfterNext.plus(DatePeriod(days = daysUntilNextMonday))
        val nextSunday = nextMonday.plus(DatePeriod(days = 6))
        val nextWeekStart = nextMonday.atTime(0, 0).toInstant(timeZone)
        val nextWeekEnd = nextSunday.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val nextWeekRange = nextWeekStart..nextWeekEnd

        return DateRanges(
            today = todayRange,
            nextDay = nextDayRange,
            thisWeek = thisWeekRange,
            nextWeek = nextWeekRange
        )
    }
}
