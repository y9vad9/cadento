package cadento.timers.domain

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Represents the configuration for a Pomodoro timer session.
 *
 * This data class encapsulates various settings that govern the behavior of a Pomodoro timer,
 * including focus/break durations, long break strategies, preparation phases, and confirmation behavior.
 *
 * Each setting is validated on construction to ensure the constraints hold:
 * - If preparation is enabled, the preparation time must be greater than 5 seconds.
 * - If confirmation is required, the timeout must be greater than 5 seconds.
 * - If long breaks are enabled:
 *   - The long break duration must be longer than the short break.
 *   - The number of short breaks before a long break must be > 1.
 *
 * Use helper methods such as [enablePreparation], [requireConfirmation], and [updateBreakStrategy]
 * to modify settings while preserving domain invariants.
 *
 * @throws IllegalArgumentException if any of the domain constraints are violated.
 */
data class PomodoroTimerSettings @Throws(IllegalArgumentException::class) constructor(
    val pomodoroFocusTime: PomodoroFocusTime = PomodoroFocusTime.createOrThrow(25.minutes),

    val pomodoroShortBreakTime: PomodoroShortBreakTime = PomodoroShortBreakTime.createOrThrow(5.minutes),
    val longBreakTime: PomodoroLongBreakTime = PomodoroLongBreakTime.createOrThrow(10.minutes),
    val longBreakPer: PomodoroLongBreakPerShortBreaksCount = PomodoroLongBreakPerShortBreaksCount.DEFAULT,
    val isLongBreakEnabled: Boolean = true,

    val isPreparationStateEnabled: Boolean = false,
    val preparationTime: PomodoroPreparationTime = PomodoroPreparationTime.createOrThrow(10.seconds),

    val requiresConfirmationBeforeStart: Boolean = false,
    val confirmationTimeoutTime: PomodoroConfirmationTimeoutTime = PomodoroConfirmationTimeoutTime
        .createOrThrow(30.seconds),
) {
    init {
        if (isLongBreakEnabled) {
            require(longBreakTime.duration > pomodoroShortBreakTime.duration) {
                "Long break time must be greater than short break time if long breaks are enabled."
            }
        }
    }

    /**
     * Enables the preparation phase with the provided preparation time.
     */
    fun enablePreparation(preparationTime: PomodoroPreparationTime): PomodoroTimerSettings {
        return copy(
            isPreparationStateEnabled = true,
            preparationTime = preparationTime,
        )
    }

    /**
     * Disables the preparation phase.
     */
    fun disablePreparation(): PomodoroTimerSettings {
        return copy(isPreparationStateEnabled = false)
    }

    /**
     * Enables start confirmation with a custom timeout.
     */
    fun requireConfirmation(timeout: PomodoroConfirmationTimeoutTime): PomodoroTimerSettings {
        return copy(
            requiresConfirmationBeforeStart = true,
            confirmationTimeoutTime = timeout,
        )
    }

    /**
     * Disables start confirmation.
     */
    fun skipConfirmation(): PomodoroTimerSettings {
        return copy(requiresConfirmationBeforeStart = false)
    }

    /**
     * Disables long break strategy (removes long breaks from schedule).
     */
    fun disableLongBreaks(): PomodoroTimerSettings {
        return copy(isLongBreakEnabled = false)
    }

    /**
     * Enables long breaks with the provided strategy.
     */
    fun enableLongBreaks(
        longBreakTime: PomodoroLongBreakTime,
        longBreakPer: PomodoroLongBreakPerShortBreaksCount,
    ): PomodoroTimerSettings {
        return copy(
            isLongBreakEnabled = true,
            longBreakTime = longBreakTime,
            longBreakPer = longBreakPer,
        )
    }

    /**
     * Changes the core Pomodoro focus and break timings.
     * Will still be validated against any enabled long-break or prep settings.
     */
    fun updateFocusAndShortBreak(
        focusTime: PomodoroFocusTime,
        shortBreakTime: PomodoroShortBreakTime,
    ): PomodoroTimerSettings {
        return copy(
            pomodoroFocusTime = focusTime,
            pomodoroShortBreakTime = shortBreakTime,
        )
    }
}
