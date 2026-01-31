package timemate.client.timers.domain

import kotlin.jvm.JvmInline

/**
 * Represents the count of short breaks that have occurred since the most recent reset point
 * in the Pomodoro timer cycle.
 *
 * A "reset point" is defined as the latest occurrence of either a [PomodoroTimerState.LongBreak]
 * or a [PomodoroTimerState.Inactive] state, after which the current streak of short breaks
 * is tracked.
 *
 * This value class encapsulates the number of consecutive short breaks taken since that reset.
 *
 * @property int The number of short breaks since the last reset.
 */
@JvmInline
value class PomodoroShortBreaksCountSinceBreakReset private constructor(
    val int: Int,
) {
    companion object Companion {
        const val MIN_VALUE: Int = 0

        fun create(value: Int): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(PomodoroShortBreaksCountSinceBreakReset(value))
            }
        }

        fun createOrThrow(value: Int): PomodoroShortBreaksCountSinceBreakReset {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.count
                is CreationResult.Negative ->
                    throw IllegalArgumentException("Pomodoro short breaks count since reset cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val count: PomodoroShortBreaksCountSinceBreakReset) : CreationResult
        data object Negative : CreationResult
    }
}
