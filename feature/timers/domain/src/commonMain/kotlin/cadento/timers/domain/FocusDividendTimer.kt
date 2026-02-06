package cadento.timers.domain

import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.Timer
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.TimerStateTransition
import kotlin.time.Duration
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
 * @property settings Configuration for this timer.
 * @property balance The accumulated free time balance.
 */
data class FocusDividendTimer(
    override val id: TimerId,
    override val name: TimerName,
    override val creationTime: Instant,
    override val state: FocusDividendTimerState,
    override val linkedTask: LinkedTimerTask?,
    val settings: FocusDividendTimerSettings,
    val balance: Duration = Duration.ZERO,
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
     * Starts the earning process for this timer.
     *
     * @param at The time at which the earning process starts.
     * @return A new [FocusDividendTimer] instance with updated state.
     */
    fun start(at: Instant): FocusDividendTimer {
        val transition = when (val currentState = state) {
            is FocusDividendTimerState.Terminated -> currentState.earn(at)
            is FocusDividendTimerState.Spending -> currentState.earn(at)
            is FocusDividendTimerState.Earning -> null
        }
        return if (transition != null) applyTransition(transition) else this
    }

    /**
     * Stops the timer and finalizes the current state.
     *
     * @param at The time at which the timer is stopped.
     * @return A new [FocusDividendTimer] instance with updated state and balance.
     */
    fun stop(at: Instant): FocusDividendTimer {
        return when (val currentState = state) {
            is FocusDividendTimerState.Earning -> {
                val spendTransition = currentState.spend(at)
                applyTransition(spendTransition).stop(at)
            }
            is FocusDividendTimerState.Spending -> {
                applyTransition(currentState.terminate(at))
            }
            is FocusDividendTimerState.Terminated -> this
        }
    }
    
    fun applyTransition(
        transition: TimerStateTransition<out FocusDividendTimerState, out FocusDividendTimerState>
    ): FocusDividendTimer {
        val oldState = transition.updatedOldState
        var newBalance = balance

        if (oldState is FocusDividendTimerState.Earning && oldState.endTime != null) {
            val duration = oldState.endTime - oldState.startTime
            newBalance += duration * settings.earningCoefficient.value
        }
        
        return copy(state = transition.nextState, balance = newBalance)
    }

    /**
     * Links a task to this timer.
     *
     * @param task The task to be linked.
     * @return A new [FocusDividendTimer] with the task linked.
     */
    override fun linkTask(task: LinkedTimerTask): FocusDividendTimer {
        return copy(linkedTask = task)
    }

    /**
     * Removes the linked task from this timer.
     *
     * @throws IllegalArgumentException if no task is currently linked.
     * @return A new [FocusDividendTimer] with no linked task.
     */
    @Throws(IllegalArgumentException::class)
    override fun unlinkTask(): FocusDividendTimer {
        require(linkedTask != null) { "Timer has no linked task" }
        return copy(linkedTask = null)
    }
}
