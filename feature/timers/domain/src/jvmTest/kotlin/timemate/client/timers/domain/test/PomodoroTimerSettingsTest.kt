package timemate.client.timers.domain.test

import timemate.client.timers.domain.PomodoroTimerSettings
import timemate.client.timers.domain.PomodoroLongBreakPerShortBreaksCount
import timemate.client.timers.domain.PomodoroConfirmationTimeoutTime
import timemate.client.timers.domain.PomodoroLongBreakTime
import timemate.client.timers.domain.PomodoroPreparationTime
import timemate.client.timers.domain.PomodoroShortBreakTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroTimerSettingsTest {

    private fun validSettings(): PomodoroTimerSettings =
        PomodoroTimerSettings()

    @Test
    fun `default settings are valid`() {
        // GIVEN / WHEN
        val settings = PomodoroTimerSettings()

        // THEN
        assertTrue(settings.isPreparationStateEnabled.not())
        assertTrue(settings.isLongBreakEnabled)
        assertEquals(25.minutes, settings.pomodoroFocusTime.duration)
    }

    @Test
    fun `fails when long break is shorter than short break`() {
        // GIVEN
        val longBreak = PomodoroLongBreakTime.createOrThrow(4.minutes)
        val shortBreak = PomodoroShortBreakTime.createOrThrow(5.minutes)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            PomodoroTimerSettings(
                longBreakTime = longBreak,
                pomodoroShortBreakTime = shortBreak
            )
        }

        assertTrue("Long break time must be greater" in exception.message.orEmpty())
    }

    @Test
    fun `enablePreparation updates flags and value`() {
        // GIVEN
        val newPrep = PomodoroPreparationTime.createOrThrow(6.seconds)

        // WHEN
        val updated = validSettings().enablePreparation(newPrep)

        // THEN
        assertTrue(updated.isPreparationStateEnabled)
        assertEquals(newPrep, updated.preparationTime)
    }

    @Test
    fun `disablePreparation resets flag only`() {
        // GIVEN
        val initial = validSettings().enablePreparation(
            PomodoroPreparationTime.createOrThrow(6.seconds)
        )

        // WHEN
        val updated = initial.disablePreparation()

        // THEN
        assertFalse(updated.isPreparationStateEnabled)
        assertEquals(initial.preparationTime, updated.preparationTime) // unchanged
    }

    @Test
    fun `requireConfirmation sets confirmation and timeout`() {
        // GIVEN
        val timeout = PomodoroConfirmationTimeoutTime.createOrThrow(5.seconds)

        // WHEN
        val updated = validSettings().requireConfirmation(timeout)

        // THEN
        assertTrue(updated.requiresConfirmationBeforeStart)
        assertEquals(timeout, updated.confirmationTimeoutTime)
    }

    @Test
    fun `skipConfirmation disables flag only`() {
        // GIVEN
        val initial = validSettings().requireConfirmation(
            PomodoroConfirmationTimeoutTime.createOrThrow(10.seconds)
        )

        // WHEN
        val updated = initial.skipConfirmation()

        // THEN
        assertFalse(updated.requiresConfirmationBeforeStart)
        assertEquals(initial.confirmationTimeoutTime, updated.confirmationTimeoutTime)
    }

    @Test
    fun `enableLongBreaks sets strategy`() {
        // GIVEN
        val longBreak = PomodoroLongBreakTime.createOrThrow(15.minutes)
        val count = PomodoroLongBreakPerShortBreaksCount.createOrThrow(4)

        // WHEN
        val updated = validSettings().enableLongBreaks(longBreak, count)

        // THEN
        assertTrue(updated.isLongBreakEnabled)
        assertEquals(longBreak, updated.longBreakTime)
        assertEquals(count, updated.longBreakPer)
    }

    @Test
    fun `disableLongBreaks sets flag false`() {
        // GIVEN
        val settings = validSettings()

        // WHEN
        val updated = settings.disableLongBreaks()

        // THEN
        assertFalse(updated.isLongBreakEnabled)
    }
}
