package cadento.timers.domain.unittest

import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import cadento.timers.domain.PomodoroTimerState.AwaitsConfirmation
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
