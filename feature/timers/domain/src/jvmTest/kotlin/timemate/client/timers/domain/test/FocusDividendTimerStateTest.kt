package timemate.client.timers.domain.test

import timemate.client.timers.domain.FocusDividendTimerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class FocusDividendTimerStateTest {

    private val initialTime = Instant.parse("2025-06-01T10:00:00Z")
    private val laterTime = Instant.parse("2025-06-01T10:30:00Z")

    // Helpers for BDD clarity
    private fun earningWithEnd(endTime: Instant?) = FocusDividendTimerState.Earning(initialTime, endTime)
    private fun spendingWithEnd(endTime: Instant?) = FocusDividendTimerState.Spending(initialTime, endTime)
    private fun terminatedWithEnd(endTime: Instant?) = FocusDividendTimerState.Terminated(initialTime, endTime)

    @Test
    fun `GIVEN Earning with null endTime WHEN spend is called THEN transition to Spending and sets endTime`() {
        // GIVEN
        val earning = earningWithEnd(null)

        // WHEN
        val result = earning.spend(laterTime)

        // THEN
        assertEquals(laterTime, result.updatedOldState.endTime, "Earning's endTime should be set to 'at'")
        assertEquals(laterTime, result.nextState.startTime, "Spending startTime should be 'at'")
        assertNull(result.nextState.endTime, "Spending endTime should be null")
    }

    @Test
    fun `GIVEN Earning with non-null endTime WHEN spend is called THEN throws IllegalArgumentException`() {
        // GIVEN
        val earning = earningWithEnd(laterTime)

        // WHEN / THEN
        val ex = assertFailsWith<IllegalArgumentException> {
            earning.spend(laterTime)
        }
        assertEquals("State is finalized and cannot transition further", ex.message)
    }

    @Test
    fun `GIVEN Spending with null endTime WHEN earn is called THEN transition to Earning and sets endTime`() {
        // GIVEN
        val spending = spendingWithEnd(null)

        // WHEN
        val result = spending.earn(laterTime)

        // THEN
        assertEquals(laterTime, result.updatedOldState.endTime)
        assertEquals(laterTime, result.nextState.startTime)
        assertNull(result.nextState.endTime)
    }

    @Test
    fun `GIVEN Spending with non-null endTime WHEN earn is called THEN throws IllegalArgumentException`() {
        // GIVEN
        val spending = spendingWithEnd(laterTime)

        // WHEN / THEN
        val ex = assertFailsWith<IllegalArgumentException> {
            spending.earn(laterTime)
        }
        assertEquals("State is finalized and cannot transition further", ex.message)
    }

    @Test
    fun `GIVEN Spending with null endTime WHEN terminate is called THEN transition to Terminated and sets endTime`() {
        // GIVEN
        val spending = spendingWithEnd(null)

        // WHEN
        val result = spending.terminate(laterTime)

        // THEN
        assertEquals(laterTime, result.updatedOldState.endTime)
        assertEquals(laterTime, result.nextState.startTime)
        assertNull(result.nextState.endTime)
    }

    @Test
    fun `GIVEN Spending with non-null endTime WHEN terminate is called THEN throws IllegalArgumentException`() {
        // GIVEN
        val spending = spendingWithEnd(laterTime)

        // WHEN / THEN
        val ex = assertFailsWith<IllegalArgumentException> {
            spending.terminate(laterTime)
        }
        assertEquals("State is finalized and cannot transition further", ex.message)
    }

    @Test
    fun `GIVEN Terminated with null endTime WHEN earn is called THEN transition to Earning and sets endTime`() {
        // GIVEN
        val terminated = terminatedWithEnd(null)

        // WHEN
        val result = terminated.earn(laterTime)

        // THEN
        assertEquals(laterTime, result.updatedOldState.endTime)
        assertEquals(laterTime, result.nextState.startTime)
        assertNull(result.nextState.endTime)
    }

    @Test
    fun `GIVEN Terminated with non-null endTime WHEN earn is called THEN throws IllegalArgumentException`() {
        // GIVEN
        val terminated = terminatedWithEnd(laterTime)

        // WHEN / THEN
        val ex = assertFailsWith<IllegalArgumentException> {
            terminated.earn(laterTime)
        }
        assertEquals("State is finalized and cannot transition further", ex.message)
    }

    @Test
    fun `GIVEN Earning WHEN created THEN startTime is set and endTime is null by default`() {
        // GIVEN
        val earning = FocusDividendTimerState.Earning(initialTime, null)

        // THEN
        assertEquals(initialTime, earning.startTime)
        assertNull(earning.endTime)
    }

    @Test
    fun `GIVEN Spending WHEN created THEN startTime is set and endTime is null by default`() {
        // GIVEN
        val spending = FocusDividendTimerState.Spending(initialTime, null)

        // THEN
        assertEquals(initialTime, spending.startTime)
        assertNull(spending.endTime)
    }

    @Test
    fun `GIVEN Terminated WHEN created THEN startTime is set and endTime is null by default`() {
        // GIVEN
        val terminated = FocusDividendTimerState.Terminated(initialTime, null)

        // THEN
        assertEquals(initialTime, terminated.startTime)
        assertNull(terminated.endTime)
    }
}
