package timemate.tasks.domain

import timemate.tasks.domain.Task.Companion.create
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Represents a Task in the domain with identity, descriptive attributes, status, and timing information.
 *
 * A [Task] instance can only be created through the factory methods in the companion object,
 * ensuring all domain invariants are validated at creation time.
 *
 * @property id Unique identifier of the task.
 * @property name Name of the task.
 * @property description Detailed description of the task.
 * @property status Current status of the task.
 * @property creationTime The time when the task was created.
 * @property dueTime The deadline by which the task should be completed.
 */
@Suppress("detekt.TooManyFunctions")
class Task private constructor(
    val id: TaskId,
    val name: TaskName,
    val description: TaskDescription,
    val status: TaskStatus,
    val tags: List<TaskTag>,
    val creationTime: Instant,
    val dueTime: Instant,
) {
    companion object {

        /**
         * Attempts to create a [Task] while validating all domain invariants.
         *
         * This method never throws and instead returns a typed [CreationResult]
         * describing either successful creation or the reason for failure.
         */
        @Suppress("detekt.LongParameterList")
        fun create(
            id: TaskId,
            name: TaskName,
            description: TaskDescription,
            status: TaskStatus = TaskStatus.Builtin.Planned,
            tags: List<TaskTag>,
            creationTime: Instant,
            dueTime: Instant,
        ): CreationResult {
            if (creationTime > dueTime) {
                return CreationResult.InvalidTimeRange
            }

            return CreationResult.Success(
                Task(
                    id = id,
                    name = name,
                    description = description,
                    status = status,
                    tags = tags,
                    creationTime = creationTime,
                    dueTime = dueTime,
                )
            )
        }

        /**
         * Creates a [Task] or throws an [IllegalArgumentException] if creation fails.
         *
         * This is a convenience wrapper over [create] for call sites that prefer
         * exception-based control flow.
         *
         * @throws IllegalArgumentException if any domain invariant is violated.
         */
        @Throws(IllegalArgumentException::class)
        @Suppress("detekt.LongParameterList")
        fun createOrThrow(
            id: TaskId,
            name: TaskName,
            description: TaskDescription,
            status: TaskStatus = TaskStatus.Builtin.Planned,
            tags: List<TaskTag>,
            creationTime: Instant,
            dueTime: Instant,
        ): Task =
            when (
                val result = create(
                    id = id,
                    name = name,
                    description = description,
                    status = status,
                    tags = tags,
                    creationTime = creationTime,
                    dueTime = dueTime
                )
            ) {
                is CreationResult.Success -> result.task
                CreationResult.InvalidTimeRange ->
                    throw IllegalArgumentException(
                        "Task creation time must not be after due time."
                    )
            }
    }

    /**
     * Returns a copy of this task with a new name.
     */
    fun rename(newName: TaskName): Task =
        copy(name = newName)

    /**
     * Returns a copy of this task with a new description.
     */
    fun changeDescription(newDescription: TaskDescription): Task =
        copy(description = newDescription)

    /**
     * Returns a copy of this task with a new due time.
     * @throws IllegalArgumentException if the new due time is before the creation time.
     */
    fun changeDueTime(newDueTime: Instant): ChangeDueTimeResult {
        if (newDueTime < creationTime) {
            return ChangeDueTimeResult.InvalidTimeRange
        }
        return ChangeDueTimeResult.Success(copy(dueTime = newDueTime))
    }

    /**
     * Returns a copy of this task with updated tags.
     */
    fun updateTags(newTags: List<TaskTag>): Task =
        copy(tags = newTags)

    /**
     * Returns the duration remaining until the task is due, relative to [currentTime].
     *
     * @throws IllegalArgumentException if the task is already overdue at [currentTime].
     */
    fun dueIn(currentTime: Instant): Duration {
        require(isDue(currentTime)) { "Task should not be overdue." }
        return dueTime - currentTime
    }

    /**
     * Checks if the task is currently due (not overdue).
     */
    fun isDue(currentTime: Instant): Boolean =
        !isOverdue(currentTime)

    /**
     * Checks if the task is overdue at the given [currentTime] and not marked as done.
     */
    fun isOverdue(currentTime: Instant): Boolean =
        dueTime < currentTime && status != TaskStatus.Builtin.Done

    /**
     * Returns a copy of this task with its status updated to the specified [status].
     */
    fun markAs(status: TaskStatus): Task =
        if (status == this.status) this else copy(status = status)

    /**
     * Returns a copy of this task marked as done.
     */
    fun markAsDone(): Task =
        markAs(status = TaskStatus.Builtin.Done)

    override fun equals(other: Any?): Boolean =
        this === other || (other is Task && other.id == id)

    override fun hashCode(): Int = id.hashCode()

    /**
     * Result of attempting to change the due time of a [Task].
     */
    sealed interface ChangeDueTimeResult {
        data class Success(val task: Task) : ChangeDueTimeResult
        data object InvalidTimeRange : ChangeDueTimeResult
    }

    /**
     * Result of attempting to create a [Task].
     */
    sealed interface CreationResult {

        /**
         * Indicates successful task creation.
         */
        data class Success(
            val task: Task,
        ) : CreationResult

        /**
         * Indicates that the provided time range is invalid.
         */
        data object InvalidTimeRange : CreationResult
    }

    /**
     * Creates a modified copy of this task.
     * This is intentionally private to keep mutation rules inside the aggregate.
     */
    @Suppress("detekt.LongParameterList")
    private fun copy(
        id: TaskId = this.id,
        name: TaskName = this.name,
        description: TaskDescription = this.description,
        status: TaskStatus = this.status,
        tags: List<TaskTag> = this.tags,
        creationTime: Instant = this.creationTime,
        dueTime: Instant = this.dueTime,
    ): Task =
        Task(
            id = id,
            name = name,
            description = description,
            status = status,
            tags = tags,
            creationTime = creationTime,
            dueTime = dueTime,
        )
}
