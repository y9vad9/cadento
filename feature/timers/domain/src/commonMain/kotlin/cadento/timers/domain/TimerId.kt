package cadento.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TimerId private constructor(
    val long: Long,
) {
    companion object {
        const val MIN_VALUE: Long = 0

        fun create(value: Long): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(TimerId(value))
            }
        }

        fun createOrThrow(value: Long): TimerId {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.timerId
                is CreationResult.Negative -> throw IllegalArgumentException("Timer ID cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val timerId: TimerId) : CreationResult
        data object Negative : CreationResult
    }
}
