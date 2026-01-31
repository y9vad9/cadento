package timemate.client.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class PomodoroLongBreakPerShortBreaksCount private constructor(
    val int: Int,
) {
    companion object {
        const val MIN_VALUE = 2

        val DEFAULT: PomodoroLongBreakPerShortBreaksCount = PomodoroLongBreakPerShortBreaksCount(4)

        fun create(value: Int): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.TooSmall
            } else {
                CreationResult.Success(PomodoroLongBreakPerShortBreaksCount(value))
            }
        }

        fun createOrThrow(value: Int): PomodoroLongBreakPerShortBreaksCount {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.count
                is CreationResult.TooSmall ->
                    throw IllegalArgumentException("Pomodoro long break per short breaks count must be at least " +
                        "$MIN_VALUE.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val count: PomodoroLongBreakPerShortBreaksCount) : CreationResult
        data object TooSmall : CreationResult
    }
}
