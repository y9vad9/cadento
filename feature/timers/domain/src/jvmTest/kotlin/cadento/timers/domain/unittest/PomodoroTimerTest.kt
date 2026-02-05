package cadento.timers.domain.unittest

import cadento.timers.domain.LinkedTaskId
import cadento.timers.domain.LinkedTaskName
import cadento.timers.domain.LinkedTimerTask
import cadento.timers.domain.PomodoroConfirmationTimeoutTime
import cadento.timers.domain.PomodoroFocusTime
import cadento.timers.domain.PomodoroLongBreakPerShortBreaksCount
import cadento.timers.domain.PomodoroLongBreakTime
import cadento.timers.domain.PomodoroPreparationTime
import cadento.timers.domain.PomodoroShortBreakTime
import cadento.timers.domain.PomodoroTimer
import cadento.timers.domain.PomodoroTimerSettings
import cadento.timers.domain.PomodoroTimerState
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class PomodoroTimerTest {
    private val initialTime = Instant.fromEpochMilliseconds(0)
    private val laterTime = Instant.fromEpochMilliseconds(1_000)

    @Test
    fun `transition applies the given state transition`() {
        // GIVEN
        val state = PomodoroTimerState.Inactive(initialTime)
        val settings = defaultSettings()
        val timer = createTimer(state, settings)
        val newState = state.start(laterTime, settings).nextState

        // WHEN
        val transitioned = timer.transition { newState }

        // THEN
        assertEquals(newState, transitioned.state)
    }

    @Test
    fun `linkTask correctly updates the linked task`() {
        // GIVEN
        val timer = createTimer()
        val task = createTask()

        // WHEN
        val updated = timer.linkTask(task)

        // THEN
        assertEquals(task, updated.linkedTask)
    }

    @Test
    fun `unlinkTask removes the task if linked`() {
        // GIVEN
        val task = createTask()
        val timer = createTimer().linkTask(task)

        // WHEN
        val updated = timer.unlinkTask()

        // THEN
        assertNull(updated.linkedTask)
    }

    @Test
    fun `unlinkTask throws if no task linked`() {
        // GIVEN
        val timer = createTimer()

        // WHEN / THEN
        assertFailsWith<IllegalArgumentException> {
            timer.unlinkTask()
        }
    }

    private fun createTimer(
        state: PomodoroTimerState = PomodoroTimerState.Inactive(initialTime),
        settings: PomodoroTimerSettings = defaultSettings(),
    ): PomodoroTimer {
        return PomodoroTimer(
            id = TimerId.createOrThrow(1),
            name = TimerName.createOrThrow("Test Timer"),
            creationTime = initialTime,
            state = state,
            linkedTask = null,
            settings = settings,
        )
    }

    private fun createTask(): LinkedTimerTask {
        return LinkedTimerTask(
            id = LinkedTaskId.createOrThrow(1),
            name = LinkedTaskName.createOrThrow("Test Task"),
            creationTime = initialTime,
            dueTime = initialTime + 30.minutes,
        )
    }

    private fun defaultSettings(): PomodoroTimerSettings =
        PomodoroTimerSettings(
            pomodoroFocusTime = PomodoroFocusTime.createOrThrow(
                25.minutes
            ),
            pomodoroShortBreakTime = PomodoroShortBreakTime.createOrThrow(
                5.minutes
            ),
            longBreakTime = PomodoroLongBreakTime.createOrThrow(
                10.minutes
            ),
            longBreakPer = PomodoroLongBreakPerShortBreaksCount.DEFAULT,
            isLongBreakEnabled = true,
            isPreparationStateEnabled = false,
            preparationTime = PomodoroPreparationTime.createOrThrow(
                10.seconds
            ),
            requiresConfirmationBeforeStart = false,
            confirmationTimeoutTime = PomodoroConfirmationTimeoutTime.createOrThrow(
                30.seconds
            ),
        )
}
