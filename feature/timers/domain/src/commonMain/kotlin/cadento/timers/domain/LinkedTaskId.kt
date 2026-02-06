package cadento.timers.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

/**
 * A type-safe identifier for a task linked to a timer.
 *
 * @property value The unique identifier.
 */
@JvmInline
value class LinkedTaskId(val value: Uuid)
