package cadento.timers.domain

import kotlin.time.Instant

sealed interface TimerState {
    val startTime: Instant
    val endTime: Instant?
}

