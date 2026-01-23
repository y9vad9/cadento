package timemate.client.tasks.domain

import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Represents a Task in the domain with identity, descriptive attributes, status, and timing information.
 *
 * @property id Unique identifier of the task.
 * @property name Name of the task.
 * @property description Detailed description of the task.
 * @property status Current status of the task. Defaults to [TaskStatus.Builtin.Planned].
 * @property creationTime The time when the task was created.
 * @property dueTime The deadline by which the task should be completed.
 *
 * @throws IllegalArgumentException if [creationTime] is after [dueTime].
 */
data class Task @Throws(IllegalArgumentException::class) constructor(
    val id: TaskId,
    val name: TaskName,
    val description: TaskDescription,
    val status: TaskStatus = TaskStatus.Builtin.Planned,
    val creationTime: Instant,
    val dueTime: Instant,
) {
    init {
        require(creationTime <= dueTime) {
            "Due time should equal or be greater than creation time"
        }
    }

    /**
     * Returns the duration remaining until the task is due, relative to [currentTime].
     *
     * @param currentTime The current time to compare against.
     * @return Duration until the task's due time.
     * @throws IllegalArgumentException if the task is already overdue at [currentTime].
     */
    @Throws(IllegalArgumentException::class)
    fun dueIn(currentTime: Instant): Duration {
        require(isDue(currentTime)) { "Task should not be overdue." }
        return dueTime - currentTime
    }

    /**
     * Checks if the task is currently due (not overdue).
     *
     * @param currentTime The current time to compare against.
     * @return `true` if the task is not overdue, `false` otherwise.
     */
    fun isDue(currentTime: Instant): Boolean {
        return !isOverdue(currentTime)
    }

    /**
     * Checks if the task is overdue at the given [currentTime] and not marked as done.
     *
     * @param currentTime The current time to compare against.
     * @return `true` if the task is overdue and not completed, `false` otherwise.
     */
    fun isOverdue(currentTime: Instant): Boolean {
        return dueTime < currentTime && status != TaskStatus.Builtin.Done
    }

    /**
     * Returns a copy of this task with its status updated to the specified [status].
     * If the given status is the same as the current status, returns this instance.
     *
     * @param status The new status to assign to the task.
     * @return A task instance with updated status or this instance if no change.
     */
    fun markAs(status: TaskStatus): Task {
        return if (status == this.status) this else copy(status = status)
    }

    /**
     * Returns a copy of this task marked as done.
     *
     * @return A task instance with status set to [TaskStatus.Builtin.Done].
     */
    fun markAsDone(): Task = markAs(status = TaskStatus.Builtin.Done)
}
