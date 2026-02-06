package cadento.timers.domain



import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@JvmInline
value class PomodoroPreparationTime private constructor(
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
                else -> CreationResult.Success(PomodoroPreparationTime(value))
            }
        }

        fun createOrThrow(value: Duration): PomodoroPreparationTime {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.time
                is CreationResult.TooShort ->
                    throw IllegalArgumentException("Pomodoro preparation time must be at least $MIN_TIME.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Pomodoro preparation time cannot exceed $MAX_TIME.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val time: PomodoroPreparationTime) : CreationResult
        data object TooShort : CreationResult
        data object TooLong : CreationResult
    }
}
