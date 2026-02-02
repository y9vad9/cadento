package timemate.timers.domain

import kotlin.time.Instant

sealed interface Timer {
    val id: TimerId
    val name: TimerName
    val creationTime: Instant
    val state: TimerState
    val linkedTask: LinkedTimerTask?
}
