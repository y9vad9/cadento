package cadento.timers.domain

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@JvmInline
value class PomodoroShortBreakTime private constructor(
    val duration: Duration
) {
    companion object {
        val MIN_TIME: Duration = 1.minutes
        val MAX_TIME: Duration = 20.minutes

        val TIME_RANGE: ClosedRange<Duration> = MIN_TIME..MAX_TIME

        fun create(value: Duration): CreationResult {
            return when {
                value < MIN_TIME -> CreationResult.TooShort
                value > MAX_TIME -> CreationResult.TooLong
                else -> CreationResult.Success(PomodoroShortBreakTime(value))
            }
        }

        fun createOrThrow(value: Duration): PomodoroShortBreakTime {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.time
                is CreationResult.TooShort ->
                    throw IllegalArgumentException("Pomodoro short break time must be at least $MIN_TIME.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Pomodoro short break time cannot exceed $MAX_TIME.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val time: PomodoroShortBreakTime) : CreationResult
        data object TooShort : CreationResult
        data object TooLong : CreationResult
    }
}
