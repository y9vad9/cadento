package cadento.timers.domain

import cadento.timers.domain.TimerState
import cadento.timers.domain.TimerStateTransition
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

sealed interface PomodoroTimerState : TimerState {
    val startTime: Instant
    val endTime: Instant

    data class Inactive(
        override val startTime: Instant,
        override val endTime: Instant = Instant.DISTANT_FUTURE,
    ) : PomodoroTimerState {
        fun start(at: Instant, settings: PomodoroTimerSettings): TimerStateTransition<Inactive, Focus> {
            check(at > startTime) { "New state cannot be past current." }
            return TimerStateTransition(
                Inactive(startTime, at),
                Focus(at, at + settings.pomodoroFocusTime.duration),
            )
        }
    }

    data class Focus(
        override val startTime: Instant,
        override val endTime: Instant,
    ) : PomodoroTimerState {
        fun onExpiration(
            settings: PomodoroTimerSettings,
            shortBreaksCount: PomodoroShortBreaksCountSinceBreakReset,
        ): PomodoroTimerState {
            if (!settings.isLongBreakEnabled)
                return ShortBreak(
                    startTime = endTime,
                    endTime = endTime + settings.pomodoroShortBreakTime.duration,
                )

            // it's valid to have more short breaks than in settings:
            // the session might be changed on the go and we should react appropriately.
            return if (shortBreaksCount.int >= settings.longBreakPer.int) {
                LongBreak(endTime, endTime + settings.longBreakTime.duration)
            } else {
                ShortBreak(endTime, endTime + settings.pomodoroShortBreakTime.duration)
            }
        }

        fun pause(at: Instant): TimerStateTransition<Focus, Paused> {
            return TimerStateTransition(
                Focus(startTime, at),
                Paused(at)
            )
        }
    }

    data class Paused(
        override val startTime: Instant,
        override val endTime: Instant = startTime + 25.minutes,
    ) : PomodoroTimerState {
        fun onExpiration(): Inactive {
            return Inactive(endTime)
        }

        fun resume(
            settings: PomodoroTimerSettings,
            at: Instant,
        ): TimerStateTransition<Paused, PomodoroTimerState> {
            check(at > startTime) { "New state cannot be past current." }

            val oldStateUpdate = copy(endTime = at)

            return TimerStateTransition(
                oldStateUpdate,
                nextConfirmationOrPreparationOrFocusState(
                    settings,
                    at
                )
            )
        }
    }

    data class ShortBreak(
        override val startTime: Instant,
        override val endTime: Instant,
    ) : PomodoroTimerState {
        fun onExpiration(
            settings: PomodoroTimerSettings,
        ): PomodoroTimerState {
            return nextConfirmationOrPreparationOrFocusState(
                settings,
                endTime
            )
        }

        fun terminate(at: Instant): TimerStateTransition<ShortBreak, Paused> {
            check(at > startTime) { "New state cannot be past current." }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Paused(at),
            )
        }
    }

    data class LongBreak(
        override val startTime: Instant,
        override val endTime: Instant,
    ) : PomodoroTimerState {
        fun onExpiration(settings: PomodoroTimerSettings): PomodoroTimerState {
            return nextConfirmationOrPreparationOrFocusState(
                settings,
                endTime
            )
        }

        fun terminate(at: Instant): TimerStateTransition<LongBreak, Paused> {
            check(at > startTime) { "New state cannot be past current." }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Paused(at),
            )
        }
    }

    /**
     * Preparation phase starts after Pause/Break/Inactive/AwaitsConfirmation states.
     */
    data class Preparation(
        override val startTime: Instant,
        override val endTime: Instant,
    ) : PomodoroTimerState {
        fun onExpiration(settings: PomodoroTimerSettings): PomodoroTimerState {
            return Focus(endTime, endTime + settings.pomodoroFocusTime.duration)
        }

        fun pause(at: Instant): TimerStateTransition<Preparation, Paused> {
            check(at > startTime) { "New state cannot be past current." }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Paused(at),
            )
        }
    }

    data class AwaitsConfirmation(
        override val startTime: Instant,
        override val endTime: Instant,
    ) : PomodoroTimerState {
        fun onExpiration(settings: PomodoroTimerSettings): PomodoroTimerState {
            if (settings.isPreparationStateEnabled)
                return Preparation(endTime, endTime + settings.preparationTime.duration)

            return Focus(endTime, endTime + settings.pomodoroFocusTime.duration)
        }

        fun terminate(at: Instant): TimerStateTransition<AwaitsConfirmation, Paused> {
            check(at > startTime) { "New state cannot be past current." }

            return TimerStateTransition(
                updatedOldState = copy(endTime = at),
                nextState = Paused(at),
            )
        }
    }
}

private fun nextConfirmationOrPreparationOrFocusState(
    settings: PomodoroTimerSettings,
    from: Instant,
): PomodoroTimerState {
    return when {
        settings.requiresConfirmationBeforeStart ->
            PomodoroTimerState.AwaitsConfirmation(from, from + settings.confirmationTimeoutTime.duration)

        settings.isPreparationStateEnabled ->
            PomodoroTimerState.Preparation(from, from + settings.preparationTime.duration)

        else -> PomodoroTimerState.Focus(from, from + settings.pomodoroFocusTime.duration)
    }
}
