package timemate.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class PomodoroShortBreaksCount private constructor(
    val int: Int,
) {
    companion object Companion {
        const val MIN_VALUE: Int = 0

        fun create(value: Int): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(PomodoroShortBreaksCount(value))
            }
        }

        fun createOrThrow(value: Int): PomodoroShortBreaksCount {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.count
                is CreationResult.Negative ->
                    throw IllegalArgumentException("Pomodoro short breaks count cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val count: PomodoroShortBreaksCount) : CreationResult
        data object Negative : CreationResult
    }
}
