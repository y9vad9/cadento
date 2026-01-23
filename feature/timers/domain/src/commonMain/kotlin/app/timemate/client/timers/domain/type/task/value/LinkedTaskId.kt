package app.timemate.client.timers.domain.type.task.value

import kotlin.jvm.JvmInline

@JvmInline
value class LinkedTaskId private constructor(
    val long: Long,
) {
    companion object {
        const val MIN_VALUE: Long = 0

        fun create(value: Long): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(LinkedTaskId(value))
            }
        }

        fun createOrThrow(value: Long): LinkedTaskId {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.linkedTaskId
                is CreationResult.Negative -> throw IllegalArgumentException("Linked task ID cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val linkedTaskId: LinkedTaskId) : CreationResult
        data object Negative : CreationResult
    }
}
