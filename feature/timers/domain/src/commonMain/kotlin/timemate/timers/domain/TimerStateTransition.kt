package timemate.timers.domain

/**
 * This class is used when it's required for the consumer to consume updated
 * old state (due to non-timed transition) and then next state.
 */
data class TimerStateTransition<TOldUpdated : TimerState, TNew : TimerState>(
    val updatedOldState: TOldUpdated,
    val nextState: TNew,
)
