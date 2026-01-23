package timemate.client.tasks.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TaskId private constructor(
    val long: Long,
) {
    companion object {
        const val MIN_VALUE: Long = 0

        fun create(value: Long): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(TaskId(value))
            }
        }

        fun createOrThrow(value: Long): TaskId {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.taskId
                is CreationResult.Negative -> throw IllegalArgumentException("Task ID cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val taskId: TaskId) : CreationResult
        data object Negative : CreationResult
    }
}
