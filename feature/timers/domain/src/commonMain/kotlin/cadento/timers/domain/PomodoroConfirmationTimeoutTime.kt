package cadento.timers.domain

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@JvmInline
value class PomodoroConfirmationTimeoutTime private constructor(
    val duration: Duration
) {
    companion object {
        val MIN_TIME: Duration = 5.seconds
        val MAX_TIME: Duration = 5.minutes

        val TIME_RANGE: ClosedRange<Duration> = MIN_TIME..MAX_TIME

        fun create(value: Duration): CreationResult {
            return when {
                value < MIN_TIME -> CreationResult.TooShort
                value > MAX_TIME -> CreationResult.TooLong
                else -> CreationResult.Success(PomodoroConfirmationTimeoutTime(value))
            }
        }

        fun createOrThrow(value: Duration): PomodoroConfirmationTimeoutTime {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.time
                is CreationResult.TooShort ->
                    throw IllegalArgumentException("Pomodoro confirmation timeout time must be at least $MIN_TIME.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Pomodoro confirmation timeout time cannot exceed $MAX_TIME.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val time: PomodoroConfirmationTimeoutTime) : CreationResult
        data object TooShort : CreationResult
        data object TooLong : CreationResult
    }
}
