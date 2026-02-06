package cadento.timers.database

import kotlin.uuid.Uuid

data class DbTimer(
    val id: Uuid,
    val name: String,
    val type: DbTimerType,
    val createdAt: String,
    val linkedTask: DbLinkedTask?,
    val state: DbTimerState,
    val pomodoroSettings: DbPomodoroSettings?,
    val focusDividendSettings: DbFocusDividendSettings?,
)

enum class DbTimerType {
    POMODORO, REGULAR, FOCUS_DIVIDEND
}

data class DbLinkedTask(
    val id: Uuid,
    val name: String,
    val createdAt: String,
    val dueTime: String,
)

data class DbTimerState(
    val type: DbTimerStateType,
    val startTime: String,
    val endTime: String?,
)

enum class DbTimerStateType {
    // Pomodoro
    POMODORO_INACTIVE,
    POMODORO_FOCUS,
    POMODORO_PAUSED,
    POMODORO_SHORT_BREAK,
    POMODORO_LONG_BREAK,
    POMODORO_PREPARATION,
    POMODORO_AWAITS_CONFIRMATION,
    
    // Regular
    REGULAR_INACTIVE,
    REGULAR_ACTIVE,
    
    // Focus Dividend
    FOCUS_DIVIDEND_EARNING,
    FOCUS_DIVIDEND_SPENDING,
    FOCUS_DIVIDEND_TERMINATED
}

data class DbPomodoroSettings(
    val focusDuration: Long,
    val shortBreakDuration: Long,
    val longBreakDuration: Long,
    val longBreakPer: Int,
    val isLongBreakEnabled: Boolean,
    val isPreparationEnabled: Boolean,
    val preparationDuration: Long,
    val requiresConfirmation: Boolean,
    val confirmationTimeout: Long,
)

data class DbFocusDividendSettings(
    val coefficient: Double,
    val balance: Long,
)
