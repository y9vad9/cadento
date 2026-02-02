package timemate.timers.domain

import kotlin.jvm.JvmInline

@JvmInline
value class LinkedTaskName private constructor(
    val string: String,
) {
    companion object Companion {
        val LENGTH_RANGE = 1..100

        fun create(value: String): CreationResult {
            return when {
                value.length < LENGTH_RANGE.first -> CreationResult.Empty
                value.length > LENGTH_RANGE.last -> CreationResult.TooLong
                else -> CreationResult.Success(LinkedTaskName(value))
            }
        }

        fun createOrThrow(value: String): LinkedTaskName {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.linkedTaskName
                is CreationResult.Empty ->
                    throw IllegalArgumentException("Linked task name cannot be empty.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Linked task name cannot exceed ${LENGTH_RANGE.last} characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val linkedTaskName: LinkedTaskName) : CreationResult
        data object Empty : CreationResult
        data object TooLong : CreationResult
    }
}
