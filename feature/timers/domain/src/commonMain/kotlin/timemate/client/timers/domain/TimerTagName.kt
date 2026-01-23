package timemate.client.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TimerTagName private constructor(
    val string: String,
) {
    companion object {
        val LENGTH_RANGE: IntRange = 1..50

        fun create(value: String): CreationResult {
            return when {
                value.length < LENGTH_RANGE.first -> CreationResult.Empty
                value.length > LENGTH_RANGE.last -> CreationResult.TooLong
                else -> CreationResult.Success(TimerTagName(value))
            }
        }

        fun createOrThrow(value: String): TimerTagName {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.timerTagName
                is CreationResult.Empty ->
                    throw IllegalArgumentException("Timer tag name cannot be empty.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Timer tag name cannot exceed ${LENGTH_RANGE.last} characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val timerTagName: TimerTagName) : CreationResult
        data object Empty : CreationResult
        data object TooLong : CreationResult
    }
}
