package timemate.client.timers.domain

import kotlin.time.Instant

/**
 * Represents a focus dividend timer instance in the domain.
 *
 * This timer tracks the passage of time for focus sessions intended to generate time to be spent.
 * It can be linked to a task and maintains its own [TimerState] (e.g. running, paused, stopped).
 *
 * Provides safe transition methods to change state or task association while preserving immutability.
 *
 * @property id Unique identifier of the timer.
 * @property name Display name of the timer.
 * @property creationTime The time when this timer was created.
 * @property state Current state of the timer (e.g., running, paused).
 * @property linkedTask Optional task associated with this timer.
 */
data class FocusDividendTimer(
    override val id: TimerId,
    override val name: TimerName,
    override val creationTime: Instant,
    override val state: FocusDividendTimerState,
    override val linkedTask: LinkedTimerTask?,
) : Timer {

    /**
     * Transitions the current timer to a new [state], using the provided [block].
     *
     * Useful for domain-safe state updates, e.g., start, pause, complete.
     *
     * @param block A lambda that returns a new [TimerState] based on the current state.
     * @return A new [FocusDividendTimer] instance with the updated state.
     */
    inline fun transition(block: (FocusDividendTimerState) -> FocusDividendTimerState): FocusDividendTimer {
        return copy(state = block(state))
    }

    /**
     * Links a task to this timer.
     *
     * @param task The task to be linked.
     * @return A new [FocusDividendTimer] with the task linked.
     */
    fun linkTask(task: LinkedTimerTask): FocusDividendTimer {
        return copy(linkedTask = task)
    }

    /**
     * Removes the linked task from this timer.
     *
     * @throws IllegalArgumentException if no task is currently linked.
     * @return A new [FocusDividendTimer] with no linked task.
     */
    @Throws(IllegalArgumentException::class)
    fun unlinkTask(): FocusDividendTimer {
        require(linkedTask != null) { "Timer has no linked task" }
        return copy(linkedTask = null)
    }
}
