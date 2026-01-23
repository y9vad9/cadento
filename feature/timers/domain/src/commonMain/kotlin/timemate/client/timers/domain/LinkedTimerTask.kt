package timemate.client.timers.domain

import kotlin.time.Instant

class LinkedTimerTask(
    val id: LinkedTaskId,
    val name: LinkedTaskName,
    val creationTime: Instant,
    val dueTime: Instant,
)
