package cadento.timers.domain

import cadento.timers.domain.TimerState
import cadento.timers.domain.TimerStateTransition
import kotlin.time.Instant

sealed interface FocusDividendTimerState : TimerState {
    override val startTime: Instant
    override val endTime: Instant?

    data class Earning(
        override val startTime: Instant,
        override val endTime: Instant?,
    ) : FocusDividendTimerState {
        fun spend(at: Instant): TimerStateTransition<Earning, Spending> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Spending(at, null),
            )
        }
    }

    data class Spending(
        override val startTime: Instant,
        override val endTime: Instant?
    ) : FocusDividendTimerState {
        fun earn(at: Instant): TimerStateTransition<Spending, Earning> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Earning(
                    startTime = at,
                    endTime = null,
                )
            )
        }

        fun terminate(at: Instant): TimerStateTransition<Spending, Terminated> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Terminated(
                    startTime = at,
                    endTime = null,
                )
            )
        }
    }

    data class Terminated(
        override val startTime: Instant,
        override val endTime: Instant?
    ) : FocusDividendTimerState {
        fun earn(at: Instant): TimerStateTransition<Terminated, Earning> {
            // Guard: Ensure we only transition from a currently active (non-ended) state.
            // If endTime is already set, this state is completed, and no further transitions
            // should occur from it. This prevents inconsistent, unexpected and not intended by contract
            // state reuse.
            require(endTime == null) { "State is finalized and cannot transition further" }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Earning(
                    startTime = at,
                    endTime = null,
                )
            )
        }
    }
}
