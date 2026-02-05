package cadento.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TimerTagId private constructor(
    val long: Long,
) {
    companion object {
        const val MIN_VALUE: Long = 0

        fun create(value: Long): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(TimerTagId(value))
            }
        }

        fun createOrThrow(value: Long): TimerTagId {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.timerTagId
                is CreationResult.Negative -> throw IllegalArgumentException("Timer tag ID cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val timerTagId: TimerTagId) : CreationResult
        data object Negative : CreationResult
    }
}
