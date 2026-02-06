package cadento.timers.application.unittest.focusdividend

import cadento.timers.domain.FocusDividendTimer
import cadento.timers.domain.FocusDividendTimerSettings
import cadento.timers.domain.FocusDividendTimerState
import cadento.timers.domain.TimerId
import cadento.timers.domain.TimerName
import cadento.timers.domain.TimerStateTransition
import cadento.timers.domain.EarningCoefficient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class FocusDividendTimerDomainTest {

    @Test
    fun `applyTransition from Earning updates balance correctly`() {
        // GIVEN
        val startTime = Instant.parse("2024-01-01T00:00:00Z")
        val endTime = Instant.parse("2024-01-01T00:30:00Z") // 30 minutes
        val coefficientValue = 2.0
        val coefficient = EarningCoefficient.createOrThrow(coefficientValue)
        val settings = FocusDividendTimerSettings(earningCoefficient = coefficient)
        
        val timer = createTimer(startTime, FocusDividendTimerState.Earning(startTime, null), settings)
        
        val transition = TimerStateTransition(
            updatedOldState = FocusDividendTimerState.Earning(startTime, endTime),
            nextState = FocusDividendTimerState.Spending(endTime, null)
        )

        // WHEN
        val updatedTimer = timer.applyTransition(transition)

        // THEN
        val expectedAccrued = 30.minutes * coefficientValue
        assertEquals(expectedAccrued, updatedTimer.balance)
        assertEquals(FocusDividendTimerState.Spending(endTime, null), updatedTimer.state)
    }

    @Test
    fun `stop from Earning state transitions through spending to terminated`() {
        // GIVEN
        val startTime = Instant.parse("2024-01-01T00:00:00Z")
        val stopTime = Instant.parse("2024-01-01T00:30:00Z")
        val timer = createTimer(startTime, FocusDividendTimerState.Earning(startTime, null))

        // WHEN
        val stoppedTimer = timer.stop(stopTime)

        // THEN
        assert(stoppedTimer.state is FocusDividendTimerState.Terminated)
        assertEquals(15.minutes, stoppedTimer.balance)
    }

    @Test
    fun `stop from Spending state transitions to terminated`() {
        // GIVEN
        val startTime = Instant.parse("2024-01-01T00:00:00Z")
        val stopTime = Instant.parse("2024-01-01T00:30:00Z")
        val timer = createTimer(startTime, FocusDividendTimerState.Spending(startTime, null))

        // WHEN
        val stoppedTimer = timer.stop(stopTime)

        // THEN
        assert(stoppedTimer.state is FocusDividendTimerState.Terminated)
        assertEquals(Duration.ZERO, stoppedTimer.balance)
    }

    @Test
    fun `start from Terminated state transitions to Earning`() {
        // GIVEN
        val startTime = Instant.parse("2024-01-01T00:00:00Z")
        val timer = createTimer(startTime, FocusDividendTimerState.Terminated(startTime, null))

        // WHEN
        val startedTimer = timer.start(startTime + 1.minutes)

        // THEN
        assert(startedTimer.state is FocusDividendTimerState.Earning)
    }

    private fun createTimer(
        creationTime: Instant,
        state: FocusDividendTimerState,
        settings: FocusDividendTimerSettings = FocusDividendTimerSettings()
    ): FocusDividendTimer {
        return FocusDividendTimer(
            id = TimerId(Uuid.random()),
            name = TimerName.createOrThrow("Focus"),
            creationTime = creationTime,
            state = state,
            linkedTask = null,
            settings = settings,
            balance = Duration.ZERO
        )
    }
}
