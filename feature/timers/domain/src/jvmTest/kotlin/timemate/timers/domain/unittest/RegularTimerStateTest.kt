package timemate.timers.domain.unittest

import timemate.timers.domain.RegularTimerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Instant

class RegularTimerStateTest {

    @Test
    fun `Inactive start() transitions correctly to Active`() {
        // GIVEN
        val initialTime = Instant.parse("2025-06-01T09:00:00Z")
        val startAt = Instant.parse("2025-06-01T09:05:00Z")
        val inactive = RegularTimerState.Inactive(startTime = initialTime, endTime = null)

        // WHEN
        val transition = inactive.start(startAt)

        // THEN
        // The old state should have its endTime set to startAt
        assertEquals(inactive.copy(endTime = startAt), transition.updatedOldState)

        // The next state should be Active with startTime = startAt and endTime = null
        val next = transition.nextState
        assertEquals(startAt, next.startTime)
        assertEquals(null, next.endTime)
        assertIs<RegularTimerState.Active>(next)
    }

    @Test
    fun `Inactive start() throws if endTime already set`() {
        // GIVEN
        val initialTime = Instant.parse("2025-06-01T08:00:00Z")
        val alreadyEnded = Instant.parse("2025-06-01T08:30:00Z")
        val inactive = RegularTimerState.Inactive(startTime = initialTime, endTime = alreadyEnded)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            inactive.start(Instant.parse("2025-06-01T09:00:00Z"))
        }
        assertEquals("State is finalized and cannot transition further", exception.message)
    }

    @Test
    fun `Active pause() transitions correctly to Inactive`() {
        // GIVEN
        val activeStart = Instant.parse("2025-06-01T10:00:00Z")
        val pauseAt = Instant.parse("2025-06-01T10:30:00Z")
        val active = RegularTimerState.Active(startTime = activeStart, endTime = null)

        // WHEN
        val transition = active.pause(pauseAt)

        // THEN
        // The old state should have its endTime set to pauseAt
        assertEquals(active.copy(endTime = pauseAt), transition.updatedOldState)

        // The next state should be Inactive with startTime = pauseAt and endTime = null
        val next = transition.nextState
        assertEquals(pauseAt, next.startTime)
        assertEquals(null, next.endTime)
        assertIs<RegularTimerState.Inactive>(next)
    }

    @Test
    fun `Active pause() throws if endTime already set`() {
        // GIVEN
        val activeStart = Instant.parse("2025-06-01T11:00:00Z")
        val alreadyEnded = Instant.parse("2025-06-01T11:15:00Z")
        val active = RegularTimerState.Active(startTime = activeStart, endTime = alreadyEnded)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            active.pause(Instant.parse("2025-06-01T11:30:00Z"))
        }
        assertEquals("State is finalized and cannot transition further", exception.message)
    }
}
