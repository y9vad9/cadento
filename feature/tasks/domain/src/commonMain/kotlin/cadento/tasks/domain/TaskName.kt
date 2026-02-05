package cadento.tasks.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TaskName private constructor(
    val string: String,
) {
    companion object {
        val LENGTH_RANGE = 1..100

        fun create(value: String): CreationResult {
            return when {
                value.length !in LENGTH_RANGE -> CreationResult.InvalidLength
                else -> CreationResult.Success(TaskName(value))
            }
        }

        fun createOrThrow(value: String): TaskName {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.taskName
                is CreationResult.InvalidLength ->
                    throw IllegalArgumentException("Task name length must be between ${LENGTH_RANGE.first}" +
                        " and ${LENGTH_RANGE.last} characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val taskName: TaskName) : CreationResult
        data object InvalidLength : CreationResult
    }
}
