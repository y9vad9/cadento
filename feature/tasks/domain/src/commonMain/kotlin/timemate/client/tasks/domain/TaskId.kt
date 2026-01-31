package timemate.client.tasks.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

/**
 * A type-safe identifier for a [Task].
 *
 * @property value The unique identifier.
 */
@JvmInline
value class TaskId(val value: Uuid) {
    companion object {
        /**
         * Generates a new, random [TaskId].
         */
        fun random(): TaskId = TaskId(Uuid.random())
    }
}
