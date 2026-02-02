package timemate.timers.domain

import kotlin.time.Instant

data class TimerTag(
    val id: TimerTagId,
    val name: TimerTagName,
    val creationTime: Instant,
)
