package timemate.client.timers.domain

import kotlin.time.Instant

/**
 * Represents a Pomodoro-style timer.
 *
 * A [PomodoroTimer] tracks its own identity, name, creation timestamp, current state,
 * and optionally an associated task ([linkedTask]).
 *
 * This model is immutable and supports domain-specific transitions via functional-style operations.
 *
 * @property id Unique identifier for the timer.
 * @property name Human-readable name of the timer.
 * @property creationTime Timestamp of when the timer was created.
 * @property state Current lifecycle state of the timer (e.g., pending, running, paused, etc.).
 * @property linkedTask An optional task associated with this timer.
 */
data class PomodoroTimer(
    override val id: TimerId,
    override val name: TimerName,
    override val creationTime: Instant,
    override val state: PomodoroTimerState,
    override val linkedTask: LinkedTimerTask?,
    val settings: PomodoroTimerSettings,
) : Timer {

    /**
     * Returns a copy of this timer with a new [state], resulting from the given transition block.
     *
     * This method allows safe, functional transformation of the timer's state while preserving immutability.
     *
     * Example usage:
     * ```
     * timer.transition { it.start() }
     * ```
     *
     * @param block A lambda that transforms the current [PomodoroTimerState] into a new one.
     * @return A new [PomodoroTimer] instance with the updated state.
     */
    inline fun transition(block: (PomodoroTimerState) -> PomodoroTimerState): PomodoroTimer {
        return copy(state = block(state))
    }

    /**
     * Returns a copy of this timer with the given [task] associated to it.
     *
     * If a task was already linked, it will be replaced with the provided one.
     *
     * @param task The task to associate with this timer.
     * @return A new [PomodoroTimer] instance with the updated task link.
     */
    fun linkTask(task: LinkedTimerTask): PomodoroTimer {
        return copy(linkedTask = task)
    }

    /**
     * Returns a copy of this timer with the current [linkedTask] removed.
     *
     * @throws IllegalArgumentException if no task is currently linked to this timer.
     * @return A new [PomodoroTimer] instance with the task unlinked.
     */
    @Throws(IllegalArgumentException::class)
    fun unlinkTask(): PomodoroTimer {
        require(linkedTask != null) { "Timer has no linked task" }
        return copy(linkedTask = null)
    }
}

