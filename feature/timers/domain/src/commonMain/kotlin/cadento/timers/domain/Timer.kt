package cadento.timers.domain

import kotlin.time.Instant

sealed interface Timer {
    val id: TimerId
    val name: TimerName
    val creationTime: Instant
    val state: TimerState
    val linkedTask: LinkedTimerTask?

    fun linkTask(task: LinkedTimerTask): Timer
    fun unlinkTask(): Timer
}
