package cadento.core.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TagName private constructor(
    val string: String,
) {
    companion object {
        const val MIN_LENGTH: Int = 1
        const val MAX_LENGTH: Int = 50

        val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        fun create(value: String): CreationResult {
            return when {
                value.length < MIN_LENGTH -> CreationResult.Empty
                value.length > MAX_LENGTH -> CreationResult.TooLong
                else -> CreationResult.Success(TagName(value))
            }
        }

        fun createOrThrow(value: String): TagName {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.tagName
                is CreationResult.Empty ->
                    throw IllegalArgumentException("Tag name cannot be empty.")
                is CreationResult.TooLong ->
                    throw IllegalArgumentException("Tag name cannot exceed $MAX_LENGTH characters.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val tagName: TagName) : CreationResult
        data object Empty : CreationResult
        data object TooLong : CreationResult
    }
}
