package app.timemate.client.core.domain.type.value

import kotlin.jvm.JvmInline

@JvmInline
value class TagId private constructor(
    val long: Long,
) {
    companion object {
        const val MINIMAL_VALUE = 0L

        fun create(value: Long): CreationResult {
            return if (value < MINIMAL_VALUE) {
                CreationResult.Negative
            } else {
                CreationResult.Success(TagId(value))
            }
        }

        fun createOrThrow(value: Long): TagId {
            return when (val result = create(value)) {
                is CreationResult.Success -> result.tagId
                is CreationResult.Negative -> throw IllegalArgumentException("Tag ID cannot be negative.")
            }
        }
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val tagId: TagId) : CreationResult
        data object Negative : CreationResult
    }
}
