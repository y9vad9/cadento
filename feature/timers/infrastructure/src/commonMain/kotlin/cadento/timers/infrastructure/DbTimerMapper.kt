package cadento.timers.infrastructure

import cadento.timers.application.TimerFilter
import cadento.timers.application.TimerSort
import cadento.timers.database.DbFocusDividendSettings
import cadento.timers.database.DbLinkedTask
import cadento.timers.database.DbPomodoroSettings
import cadento.timers.database.DbTimer
import cadento.timers.database.DbTimerSort
import cadento.timers.database.DbTimerState
import cadento.timers.database.DbTimerStateType
import cadento.timers.database.DbTimerType
import cadento.timers.domain.EarningCoefficient
import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerSettings
import cadento.timers.domain.FocusDividendTimerState
import cadento.timers.domain.LinkedTaskId
import cadento.timers.domain.LinkedTaskName
import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.PomodoroConfirmationTimeoutTime
import cadento.timers.domain.PomodoroFocusTime
import cadento.timers.domain.PomodoroLongBreakPerShortBreaksCount
import cadento.timers.domain.PomodoroLongBreakTime
import cadento.timers.domain.PomodoroPreparationTime
import cadento.timers.domain.PomodoroShortBreakTime
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import cadento.timers.domain.RegularTimer
import cadento.timers.domain.RegularTimerState
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.time.TestTimeSource

@Suppress("detekt.TooManyFunctions")
class DbTimerMapper {

    fun mapToDb(timer: Timer): DbTimer {
        return DbTimer(
            id = timer.id.value,
            name = timer.name.string,
            type = timer.dbType,
            createdAt = timer.creationTime.toString(),
            linkedTask = timer.linkedTask?.let {
                DbLinkedTask(
                    id = it.id.value,
                    name = it.name.string,
                    createdAt = it.creationTime.toString(),
                    dueTime = it.dueTime.toString()
                )
            },
            state = mapStateToDb(timer.state),
            pomodoroSettings = (timer as? PomodoroTimer)?.settings?.let {
                DbPomodoroSettings(
                    focusDuration = it.pomodoroFocusTime.duration.inWholeMilliseconds,
                    shortBreakDuration = it.pomodoroShortBreakTime.duration.inWholeMilliseconds,
                    longBreakDuration = it.longBreakTime.duration.inWholeMilliseconds,
                    longBreakPer = it.longBreakPer.int,
                    isLongBreakEnabled = it.isLongBreakEnabled,
                    isPreparationEnabled = it.isPreparationStateEnabled,
                    preparationDuration = it.preparationTime.duration.inWholeMilliseconds,
                    requiresConfirmation = it.requiresConfirmationBeforeStart,
                    confirmationTimeout = it.confirmationTimeoutTime.duration.inWholeMilliseconds
                )
            },
            focusDividendSettings = (timer as? FocusDividendTimer)?.let {
                DbFocusDividendSettings(
                    coefficient = it.settings.earningCoefficient.value,
                    balance = it.balance.inWholeMilliseconds
                )
            }
        )
    }

    private val Timer.dbType: DbTimerType get() = when (this) {
        is PomodoroTimer -> DbTimerType.POMODORO
        is RegularTimer -> DbTimerType.REGULAR
        is FocusDividendTimer -> DbTimerType.FOCUS_DIVIDEND
    }

    private fun mapStateToDb(state: cadento.timers.domain.TimerState): DbTimerState {
        return DbTimerState(
            type = when (state) {
                is PomodoroTimerState.Inactive -> DbTimerStateType.POMODORO_INACTIVE
                is PomodoroTimerState.Focus -> DbTimerStateType.POMODORO_FOCUS
                is PomodoroTimerState.Paused -> DbTimerStateType.POMODORO_PAUSED
                is PomodoroTimerState.ShortBreak -> DbTimerStateType.POMODORO_SHORT_BREAK
                is PomodoroTimerState.LongBreak -> DbTimerStateType.POMODORO_LONG_BREAK
                is PomodoroTimerState.Preparation -> DbTimerStateType.POMODORO_PREPARATION
                is PomodoroTimerState.AwaitsConfirmation -> DbTimerStateType.POMODORO_AWAITS_CONFIRMATION
                is RegularTimerState.Inactive -> DbTimerStateType.REGULAR_INACTIVE
                is RegularTimerState.Active -> DbTimerStateType.REGULAR_ACTIVE
                is FocusDividendTimerState.Earning -> DbTimerStateType.FOCUS_DIVIDEND_EARNING
                is FocusDividendTimerState.Spending -> DbTimerStateType.FOCUS_DIVIDEND_SPENDING
                is FocusDividendTimerState.Terminated -> DbTimerStateType.FOCUS_DIVIDEND_TERMINATED
            },
            startTime = state.startTime.toString(),
            endTime = state.endTime?.toString()
        )
    }

    fun mapToDomain(dbTimer: DbTimer): Timer {
        return when (dbTimer.type) {
            DbTimerType.POMODORO -> mapPomodoroTimer(dbTimer)
            DbTimerType.REGULAR -> mapRegularTimer(dbTimer)
            DbTimerType.FOCUS_DIVIDEND -> mapFocusDividendTimer(dbTimer)
        }
    }

    private fun mapPomodoroTimer(dbTimer: DbTimer): PomodoroTimer {
        return PomodoroTimer(
            id = TimerId(dbTimer.id),
            name = TimerName.createOrThrow(dbTimer.name),
            creationTime = dbTimer.createdAt.toInstant(),
            state = mapPomodoroState(dbTimer.state),
            linkedTask = dbTimer.linkedTask?.let { mapLinkedTask(it) },
            settings = mapPomodoroSettings(dbTimer)
        )
    }

    private fun mapRegularTimer(dbTimer: DbTimer): RegularTimer {
        return RegularTimer(
            id = TimerId(dbTimer.id),
            name = TimerName.createOrThrow(dbTimer.name),
            creationTime = dbTimer.createdAt.toInstant(),
            state = mapRegularState(dbTimer.state),
            linkedTask = dbTimer.linkedTask?.let { mapLinkedTask(it) }
        )
    }

    private fun mapFocusDividendTimer(dbTimer: DbTimer): FocusDividendTimer {
        val fdSettings = requireNotNull(dbTimer.focusDividendSettings) { "focusDividendSettings is null" }
        return FocusDividendTimer(
            id = TimerId(dbTimer.id),
            name = TimerName.createOrThrow(dbTimer.name),
            creationTime = dbTimer.createdAt.toInstant(),
            state = mapFocusDividendState(dbTimer.state),
            linkedTask = dbTimer.linkedTask?.let { mapLinkedTask(it) },
            settings = FocusDividendTimerSettings(
                earningCoefficient = EarningCoefficient.createOrThrow(fdSettings.coefficient)
            ),
            balance = fdSettings.balance.milliseconds
        )
    }

    private fun mapPomodoroSettings(dbTimer: DbTimer): PomodoroTimerSettings {
        val dbSettings = requireNotNull(dbTimer.pomodoroSettings) { "pomodoroSettings is null" }
        return PomodoroTimerSettings(
            pomodoroFocusTime = PomodoroFocusTime.createOrThrow(dbSettings.focusDuration.milliseconds),
            pomodoroShortBreakTime = PomodoroShortBreakTime.createOrThrow(dbSettings.shortBreakDuration.milliseconds),
            longBreakTime = PomodoroLongBreakTime.createOrThrow(dbSettings.longBreakDuration.milliseconds),
            longBreakPer = PomodoroLongBreakPerShortBreaksCount.createOrThrow(dbSettings.longBreakPer),
            isLongBreakEnabled = dbSettings.isLongBreakEnabled,
            isPreparationStateEnabled = dbSettings.isPreparationEnabled,
            preparationTime = PomodoroPreparationTime.createOrThrow(dbSettings.preparationDuration.milliseconds),
            requiresConfirmationBeforeStart = dbSettings.requiresConfirmation,
            confirmationTimeoutTime = PomodoroConfirmationTimeoutTime.createOrThrow(
                dbSettings.confirmationTimeout.milliseconds
            )
        )
    }

    private fun mapLinkedTask(it: DbLinkedTask): LinkedTimerTask {
        return LinkedTimerTask(
            id = LinkedTaskId(it.id),
            name = LinkedTaskName.createOrThrow(it.name),
            creationTime = it.createdAt.toInstant(),
            dueTime = it.dueTime.toInstant()
        )
    }

    private fun mapPomodoroState(state: DbTimerState): PomodoroTimerState {
        val startTime = state.startTime.toInstant()
        val endTime = requireNotNull(state.endTime?.toInstant()) { "endTime is null for all pomodoro states" }
        
        return when (state.type) {
            DbTimerStateType.POMODORO_INACTIVE -> PomodoroTimerState.Inactive(startTime, endTime)
            DbTimerStateType.POMODORO_FOCUS -> PomodoroTimerState.Focus(startTime, endTime)
            DbTimerStateType.POMODORO_PAUSED -> PomodoroTimerState.Paused(startTime, endTime)
            DbTimerStateType.POMODORO_SHORT_BREAK -> PomodoroTimerState.ShortBreak(startTime, endTime)
            DbTimerStateType.POMODORO_LONG_BREAK -> PomodoroTimerState.LongBreak(startTime, endTime)
            DbTimerStateType.POMODORO_PREPARATION -> PomodoroTimerState.Preparation(startTime, endTime)
            DbTimerStateType.POMODORO_AWAITS_CONFIRMATION -> PomodoroTimerState.AwaitsConfirmation(startTime, endTime)
            else -> throw IllegalArgumentException("Unexpected pomodoro state: ${state.type}")
        }
    }

    private fun mapRegularState(state: DbTimerState): RegularTimerState {
        val startTime = state.startTime.toInstant()
        val endTime = state.endTime?.toInstant()
        return when (state.type) {
            DbTimerStateType.REGULAR_INACTIVE -> RegularTimerState.Inactive(startTime, endTime)
            DbTimerStateType.REGULAR_ACTIVE -> RegularTimerState.Active(startTime, endTime)
            else -> throw IllegalArgumentException("Unexpected regular state: ${state.type}")
        }
    }

    private fun mapFocusDividendState(state: DbTimerState): FocusDividendTimerState {
        val startTime = state.startTime.toInstant()
        val endTime = state.endTime?.toInstant()
        return when (state.type) {
            DbTimerStateType.FOCUS_DIVIDEND_EARNING -> FocusDividendTimerState.Earning(startTime, endTime)
            DbTimerStateType.FOCUS_DIVIDEND_SPENDING -> FocusDividendTimerState.Spending(startTime, endTime)
            DbTimerStateType.FOCUS_DIVIDEND_TERMINATED -> FocusDividendTimerState.Terminated(startTime, endTime)
            else -> throw IllegalArgumentException("Unexpected focus dividend state: ${state.type}")
        }
    }

    fun mapSort(sort: TimerSort): DbTimerSort {
        return when (sort) {
            TimerSort.ByCreationTimeDesc -> DbTimerSort.BY_CREATION_DESC
            TimerSort.ByNameAsc -> DbTimerSort.BY_NAME_ASC
        }
    }

    private fun String.toInstant() = Instant.parse(this)
}
