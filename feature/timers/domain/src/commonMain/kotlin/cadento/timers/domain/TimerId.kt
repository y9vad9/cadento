package cadento.timers.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

/**
 * A type-safe identifier for a [Timer].
 *
 * @property value The unique identifier.
 */
@JvmInline
value class TimerId(val value: Uuid)
