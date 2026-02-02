package timemate.timers.domain.unittest

import timemate.timers.domain.PomodoroTimerSettings
import timemate.timers.domain.PomodoroTimerState
import timemate.timers.domain.PomodoroTimerState.AwaitsConfirmation
import timemate.timers.domain.PomodoroTimerState.Inactive
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Clock

class PomodoroTimerStateAwaitsConfirmationTest {

    @Test
    fun `terminate should transition to Inactive state`() {
        // GIVEN
        val now = Clock.System.now()
        val state = AwaitsConfirmation(now, now + 1.minutes)

        // WHEN
        val transition = state.terminate(now + 30.seconds)

        // THEN
        assertIs<PomodoroTimerState.Paused>(transition.nextState)
    }

    @Test
    fun `onExpiration should transition to next appropriate state`() {
        // GIVEN
        val now = Clock.System.now()
        val state = AwaitsConfirmation(now, now + 1.minutes)
        val settings = PomodoroTimerSettings()

        // WHEN
        val newState = state.onExpiration(settings)

        // THEN
        assertIs<PomodoroTimerState.Focus>(newState)
    }
}
