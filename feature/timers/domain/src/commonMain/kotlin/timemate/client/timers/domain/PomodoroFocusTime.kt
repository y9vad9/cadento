package timemate.client.timers.domain

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@JvmInline
value class PomodoroFocusTime private constructor(
    val duration: Duration
) {
    companion object {
        val MIN_TIME: Duration = 10.minutes
        val MAX_TIME: Duration = 1.hours

        val TIME_RANGE: ClosedRange<Duration> = MIN_TIME..MAX_TIME

        fun create(value: Duration): CreationResult {
            return when {
                value < MIN_TIME -> CreationResult.TooShort
                value > MAX_TIME -> CreationResult.TooLong
                else -> CreationResult.Success(PomodoroFocusTime(value))
            }
        }

        fun createOrThrow(value: Duration): PomodoroFocusTime {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.time
                is CreationResult.TooShort ->
                    throw IllegalArgumentException("Pomodoro focus time must be at least $MIN_TIME.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Pomodoro focus time cannot exceed $MAX_TIME.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val time: PomodoroFocusTime) : CreationResult
        data object TooShort : CreationResult
        data object TooLong : CreationResult
    }
}
