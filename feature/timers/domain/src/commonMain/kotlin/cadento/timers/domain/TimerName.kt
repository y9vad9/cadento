package cadento.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TimerName private constructor(
    val string: String,
) {
    companion object {
        val LENGTH_RANGE = 1..50

        fun create(value: String): CreationResult {
            return when {
                value.length < LENGTH_RANGE.first -> CreationResult.Empty
                value.length > LENGTH_RANGE.last -> CreationResult.TooLong
                else -> CreationResult.Success(TimerName(value))
            }
        }

        fun createOrThrow(value: String): TimerName {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.timerName
                is CreationResult.Empty -> throw IllegalArgumentException("Timer name cannot be empty.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Timer name cannot exceed ${LENGTH_RANGE.last} characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val timerName: TimerName) : CreationResult
        data object Empty : CreationResult
        data object TooLong : CreationResult
    }
}
