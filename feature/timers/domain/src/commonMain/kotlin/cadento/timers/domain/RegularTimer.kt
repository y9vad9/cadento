package cadento.timers.domain

import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import kotlin.time.Instant

/**
 * Represents a regular (non-Pomodoro) timer within the domain.
 *
 * A [RegularTimer] encapsulates the state and identity of a timer that is not bound
 * to the Pomodoro technique. It tracks its creation time, current [state], and can
 * optionally be associated with a specific [linkedTask].
 *
 * This class is immutable and follows a functional style of state transitions. Modifications
 * to the timer (such as linking a task or transitioning state) return a new instance.
 *
 * @property id Unique identifier of the timer.
 * @property name Human-readable name for the timer.
 * @property creationTime The time at which the timer was created.
 * @property state The current state of the timer.
 * @property linkedTask Optional reference to a task this timer is associated with.
 */
data class RegularTimer(
    override val id: TimerId,
    override val name: TimerName,
    override val creationTime: Instant,
    override val state: RegularTimerState,
    override val linkedTask: LinkedTimerTask?,
) : Timer {

    /**
     * Transitions the current timer to a new state using the provided [block].
     *
     * This function allows encapsulating state transitions (e.g., start, pause, reset)
     * in a type-safe way, relying on [RegularTimerState] to model allowed transitions.
     *
     * @param block A lambda that receives the current state and returns the next state.
     * @return A new instance of [RegularTimer] with the updated state.
     */
    inline fun transition(block: (RegularTimerState) -> RegularTimerState): RegularTimer {
        return copy(state = block(state))
    }

    /**
     * Links the timer to a specific task.
     *
     * This creates a new instance of [RegularTimer] with the provided [task] attached.
     * It overrides any previously linked task (if present).
     *
     * @param task The task to associate with this timer.
     * @return A new instance of [RegularTimer] with the task linked.
     */
    override fun linkTask(task: LinkedTimerTask): RegularTimer {
        return copy(linkedTask = task)
    }

    /**
     * Removes the association with the currently linked task.
     *
     * @throws IllegalArgumentException if there is no task currently linked.
     * @return A new instance of [RegularTimer] with [linkedTask] set to `null`.
     */
    @Throws(IllegalArgumentException::class)
    override fun unlinkTask(): RegularTimer {
        require(linkedTask != null) { "Timer has no linked task" }
        return copy(linkedTask = null)
    }
}
