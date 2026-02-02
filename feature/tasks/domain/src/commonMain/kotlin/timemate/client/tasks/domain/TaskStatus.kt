@file:OptIn(ExperimentalContracts::class)

package timemate.client.tasks.domain

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents the status of a task in the system.
 *
 * [TaskStatus] is a sealed interface with two variants:
 * - [Builtin]: a predefined set of task statuses such as Planned, In Progress, Paused, and Done.
 * - [Custom]: a user-defined status, which must not collide with any built-in identifiers or names.
 *
 * Each status has a unique [id] and a user-facing [name], both wrapped in validated value classes.
 */
sealed interface TaskStatus {
    /** The unique identifier for this status. */
    val id: TaskStatusId

    /** The display name for this status. */
    val name: TaskStatusName

    companion object {
        /**
         * Reconstructs a [TaskStatus] instance from the given [id] and [name].
         *
         * If the [id] corresponds to a known built-in status, the appropriate [Builtin] instance is returned.
         * Otherwise, a [Custom] status is created.
         *
         * @throws IllegalArgumentException if [name] conflict with built-in values when constructing
         * a [Custom] instance.
         */
        @Throws(IllegalArgumentException::class)
        fun from(id: TaskStatusId, name: TaskStatusName): TaskStatus {
            return when (id) {
                TaskStatusId.PLANNED -> Builtin.Planned
                TaskStatusId.IN_PROGRESS -> Builtin.InProgress
                TaskStatusId.PAUSED -> Builtin.Paused
                TaskStatusId.DONE -> Builtin.Done
                else -> Custom(id, name)
            }
        }
    }

    /**
     * Represents a built-in task status with a fixed [id] and [name].
     *
     * These are predefined in the system and should not be redefined or overridden.
     */
    sealed interface Builtin : TaskStatus {
        /** Status for a task that is scheduled but not yet started. */
        data object Planned : Builtin {
            override val id: TaskStatusId = TaskStatusId.PLANNED
            override val name: TaskStatusName = TaskStatusName.PLANNED
        }

        /** Status for a task that is actively being worked on. */
        data object InProgress : Builtin {
            override val id: TaskStatusId = TaskStatusId.IN_PROGRESS
            override val name: TaskStatusName = TaskStatusName.IN_PROGRESS
        }

        /** Status for a task that has been temporarily paused. */
        data object Paused : Builtin {
            override val id: TaskStatusId = TaskStatusId.PAUSED
            override val name: TaskStatusName = TaskStatusName.PAUSED
        }

        /** Status for a task that has been completed. */
        data object Done : Builtin {
            override val id: TaskStatusId = TaskStatusId.DONE
            override val name: TaskStatusName = TaskStatusName.DONE
        }
    }

    /**
     * Represents a user-defined custom task status.
     *
     * Custom statuses must not reuse the [id] or [name] of any built-in statuses.
     * This ensures that user-defined values are clearly distinguishable from built-ins.
     *
     * @throws IllegalArgumentException if the [id] or [name] matches any built-in status.
     */
    data class Custom @Throws(IllegalArgumentException::class) constructor(
        override val id: TaskStatusId,
        override val name: TaskStatusName,
    ) : TaskStatus {
        init {
            require(!id.isBuiltin()) {
                "Custom task status cannot have any of the builtin status ids."
            }
        }
    }
}

/**
 * Returns `true` if this status is one of the built-in types.
 *
 * Enables smart casting to [TaskStatus.Builtin] via contracts.
 */
fun TaskStatus.isBuiltin(): Boolean {
    contract {
        returns(true) implies (this@isBuiltin is TaskStatus.Builtin)
    }
    return this is TaskStatus.Builtin
}

/**
 * Returns `true` if this status is a custom-defined one.
 *
 * Enables smart casting to [TaskStatus.Custom] via contracts.
 */
fun TaskStatus.isCustom(): Boolean {
    contract {
        returns(true) implies (this@isCustom is TaskStatus.Custom)
    }
    return this is TaskStatus.Custom
}

/**
 * Returns `true` if this status is [TaskStatus.Builtin.Planned].
 *
 * Enables smart casting if needed, though type is final.
 */
fun TaskStatus.isPlanned(): Boolean {
    contract {
        returns(true) implies (this@isPlanned is TaskStatus.Builtin.Planned)
    }
    return this === TaskStatus.Builtin.Planned
}

/**
 * Returns `true` if this status is [TaskStatus.Builtin.InProgress].
 */
fun TaskStatus.isInProgress(): Boolean {
    contract {
        returns(true) implies (this@isInProgress is TaskStatus.Builtin.InProgress)
    }
    return this === TaskStatus.Builtin.InProgress
}

/**
 * Returns `true` if this status is [TaskStatus.Builtin.Paused].
 */
fun TaskStatus.isPaused(): Boolean {
    contract {
        returns(true) implies (this@isPaused is TaskStatus.Builtin.Paused)
    }
    return this === TaskStatus.Builtin.Paused
}

/**
 * Returns `true` if this status is [TaskStatus.Builtin.Done].
 */
fun TaskStatus.isDone(): Boolean {
    contract {
        returns(true) implies (this@isDone is TaskStatus.Builtin.Done)
    }
    return this === TaskStatus.Builtin.Done
}
