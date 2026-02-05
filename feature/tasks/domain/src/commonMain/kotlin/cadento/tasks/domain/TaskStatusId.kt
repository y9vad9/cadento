package cadento.tasks.domain

import kotlin.jvm.JvmInline

@JvmInline
value class TaskStatusId private constructor(
    val long: Long,
) {
    companion object {
        const val MIN_VALUE: Long = -4

        fun create(value: Long): CreationResult {
            return if (value < MIN_VALUE) {
                CreationResult.TooSmall
            } else {
                CreationResult.Success(TaskStatusId(value))
            }
        }

        fun createOrThrow(value: Long): TaskStatusId {
            return when (val result = create(value)) {
                is CreationResult.Success ->
                    result.taskStatusId
                is CreationResult.TooSmall ->
                    throw IllegalArgumentException("Task status ID cannot be less than $MIN_VALUE.")
            }
        }

        val PLANNED: TaskStatusId = TaskStatusId(-4)
        val IN_PROGRESS: TaskStatusId = TaskStatusId(-3)
        val PAUSED: TaskStatusId = TaskStatusId(-2)
        val DONE: TaskStatusId = TaskStatusId(-1)

        val BUILTIN_IDS: List<TaskStatusId> = listOf(
            PLANNED, IN_PROGRESS, PAUSED, DONE,
        )
    }

    sealed interface CreationResult {
        @JvmInline
        value class Success(val taskStatusId: TaskStatusId) : CreationResult
        data object TooSmall : CreationResult
    }

    fun isNotBuiltin(): Boolean = this !in BUILTIN_IDS
    fun isBuiltin(): Boolean = !isNotBuiltin()
}
