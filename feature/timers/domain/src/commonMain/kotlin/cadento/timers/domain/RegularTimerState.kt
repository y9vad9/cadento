package cadento.timers.domain

import kotlin.time.Instant

sealed interface RegularTimerState : TimerState {
    val startTime: Instant
    val endTime: Instant?

    data class Inactive(
        override val startTime: Instant,
        override val endTime: Instant?,
    ) : RegularTimerState {
        fun start(at: Instant): TimerStateTransition<Inactive, Active> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Active(
                    startTime = at,
                    endTime = null,
                ),
            )
        }
    }

    data class Active(
        override val startTime: Instant,
        override val endTime: Instant?,
    ) : RegularTimerState {
        fun pause(at: Instant): TimerStateTransition<Active, Inactive> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }
            
            return TimerStateTransition(
                updatedOldState = copy(
                    endTime = at,
                ),
                nextState = Inactive(
                    startTime = at,
                    endTime = null,
                ),
            )
        }
    }
}
