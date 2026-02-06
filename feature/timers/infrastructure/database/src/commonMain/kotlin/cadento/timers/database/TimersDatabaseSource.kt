package cadento.timers.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import cadento.timers.sqldelight.TimerDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class TimersDatabaseSource(
    private val database: TimerDatabase,
) {
    private val queries get() = database.timerQueries

    suspend fun insertTimer(timer: DbTimer) = database.transaction {
        queries.insertTimer(
            id = timer.id.toByteArray(),
            name = timer.name,
            type = timer.type.name,
            createdAt = timer.createdAt,
            linkedTaskId = timer.linkedTask?.id?.toByteArray(),
            linkedTaskName = timer.linkedTask?.name,
            linkedTaskCreationTime = timer.linkedTask?.createdAt,
            linkedTaskDueTime = timer.linkedTask?.dueTime,
            stateType = timer.state.type.name,
            stateStartTime = timer.state.startTime,
            stateEndTime = timer.state.endTime,
            pomodoroFocusDuration = timer.pomodoroSettings?.focusDuration,
            pomodoroShortBreakDuration = timer.pomodoroSettings?.shortBreakDuration,
            pomodoroLongBreakDuration = timer.pomodoroSettings?.longBreakDuration,
            pomodoroLongBreakPer = timer.pomodoroSettings?.longBreakPer?.toLong(),
            pomodoroIsLongBreakEnabled = timer.pomodoroSettings?.isLongBreakEnabled?.toLong(),
            pomodoroIsPreparationEnabled = timer.pomodoroSettings?.isPreparationEnabled?.toLong(),
            pomodoroPreparationDuration = timer.pomodoroSettings?.preparationDuration,
            pomodoroRequiresConfirmation = timer.pomodoroSettings?.requiresConfirmation?.toLong(),
            pomodoroConfirmationTimeout = timer.pomodoroSettings?.confirmationTimeout,
            focusDividendCoefficient = timer.focusDividendSettings?.coefficient,
            focusDividendBalance = timer.focusDividendSettings?.balance
        )
    }

    suspend fun updateTimer(timer: DbTimer): DbTimer? = database.transactionWithResult {
        queries.updateTimer(
            id = timer.id.toByteArray(),
            name = timer.name,
            linkedTaskId = timer.linkedTask?.id?.toByteArray(),
            linkedTaskName = timer.linkedTask?.name,
            linkedTaskCreationTime = timer.linkedTask?.createdAt,
            linkedTaskDueTime = timer.linkedTask?.dueTime,
            stateType = timer.state.type.name,
            stateStartTime = timer.state.startTime,
            stateEndTime = timer.state.endTime,
            pomodoroFocusDuration = timer.pomodoroSettings?.focusDuration,
            pomodoroShortBreakDuration = timer.pomodoroSettings?.shortBreakDuration,
            pomodoroLongBreakDuration = timer.pomodoroSettings?.longBreakDuration,
            pomodoroLongBreakPer = timer.pomodoroSettings?.longBreakPer?.toLong(),
            pomodoroIsLongBreakEnabled = timer.pomodoroSettings?.isLongBreakEnabled?.toLong(),
            pomodoroIsPreparationEnabled = timer.pomodoroSettings?.isPreparationEnabled?.toLong(),
            pomodoroPreparationDuration = timer.pomodoroSettings?.preparationDuration,
            pomodoroRequiresConfirmation = timer.pomodoroSettings?.requiresConfirmation?.toLong(),
            pomodoroConfirmationTimeout = timer.pomodoroSettings?.confirmationTimeout,
            focusDividendCoefficient = timer.focusDividendSettings?.coefficient,
            focusDividendBalance = timer.focusDividendSettings?.balance
        )
        queries.selectTimerById(timer.id.toByteArray()).executeAsOneOrNull()?.toDbTimer()
    }

    suspend fun deleteTimer(id: Uuid): Boolean = database.transactionWithResult {
        queries.deleteTimerById(id.toByteArray()).executeAsOneOrNull() != null
    }

    fun observeTimer(id: Uuid): Flow<DbTimer?> =
        queries.selectTimerById(id.toByteArray())
            .asFlow()
            .map { it.awaitAsOneOrNull()?.toDbTimer() }

    fun observeTimers(nameContains: String?, sort: String): Flow<List<DbTimer>> =
        queries.selectTimersFiltered(nameContains, sort)
            .asFlow()
            .map { it.awaitAsList().map { row -> row.toDbTimer() } }

    private fun Boolean.toLong(): Long = if (this) 1L else 0L
    private fun Long.toBoolean(): Boolean = this == 1L

    @Suppress("LongMethod")
    private fun cadento.timers.sqldelight.PersistedTimer.toDbTimer(): DbTimer {
        val timerId = Uuid.fromByteArray(id)
        return DbTimer(
            id = timerId,
            name = name,
            type = DbTimerType.valueOf(type),
            createdAt = createdAt,
            linkedTask = linkedTaskId?.let {
                DbLinkedTask(
                    id = Uuid.fromByteArray(it),
                    name = requireNotNull(linkedTaskName) { "linkedTaskName is null for timer $timerId" },
                    createdAt = requireNotNull(linkedTaskCreationTime) {
                        "linkedTaskCreationTime is null for timer $timerId"
                    },
                    dueTime = requireNotNull(linkedTaskDueTime) {
                        "linkedTaskDueTime is null for timer $timerId"
                    }
                )
            },
            state = DbTimerState(DbTimerStateType.valueOf(stateType), stateStartTime, stateEndTime),
            pomodoroSettings = if (type == DbTimerType.POMODORO.name) {
                DbPomodoroSettings(
                    focusDuration = requireNotNull(pomodoroFocusDuration) {
                        "pomodoroFocusDuration is null for timer $timerId"
                    },
                    shortBreakDuration = requireNotNull(pomodoroShortBreakDuration) {
                        "shortBreakDuration is null for timer $timerId"
                    },
                    longBreakDuration = requireNotNull(pomodoroLongBreakDuration) {
                        "longBreakDuration is null for timer $timerId"
                    },
                    longBreakPer = requireNotNull(pomodoroLongBreakPer) {
                        "longBreakPer is null for timer $timerId"
                    }.toInt(),
                    isLongBreakEnabled = requireNotNull(pomodoroIsLongBreakEnabled) {
                        "isLongBreakEnabled is null for timer $timerId"
                    }.toBoolean(),
                    isPreparationEnabled = requireNotNull(pomodoroIsPreparationEnabled) {
                        "isPreparationEnabled is null for timer $timerId"
                    }.toBoolean(),
                    preparationDuration = requireNotNull(pomodoroPreparationDuration) {
                        "preparationDuration is null for timer $timerId"
                    },
                    requiresConfirmation = requireNotNull(pomodoroRequiresConfirmation) {
                        "requiresConfirmation is null for timer $timerId"
                    }.toBoolean(),
                    confirmationTimeout = requireNotNull(pomodoroConfirmationTimeout) {
                        "confirmationTimeout is null for timer $timerId"
                    }
                )
            } else null,
            focusDividendSettings = if (type == DbTimerType.FOCUS_DIVIDEND.name) {
                DbFocusDividendSettings(
                    coefficient = requireNotNull(focusDividendCoefficient) {
                        "focusDividendCoefficient is null for timer $timerId"
                    },
                    balance = requireNotNull(focusDividendBalance) {
                        "focusDividendBalance is null for timer $timerId"
                    }
                )
            } else null
        )
    }
}
