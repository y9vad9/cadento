package cadento.tasks.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TaskDescription private constructor(
    val string: String,
) {
    companion object {
        val LENGTH_RANGE = 1..10000

        fun create(value: String): CreationResult {
            return when {
                value.length !in LENGTH_RANGE -> CreationResult.InvalidLength
                else -> CreationResult.Success(TaskDescription(value))
            }
        }

        fun createOrThrow(value: String): TaskDescription {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.description
                is CreationResult.InvalidLength ->
                    throw IllegalArgumentException("Task description length must be between ${LENGTH_RANGE.first} and" +
                        " ${LENGTH_RANGE.last} characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val description: TaskDescription) : CreationResult
        data object InvalidLength : CreationResult
    }
}
