package timemate.coroutines.test

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import timemate.coroutines.suspendRunCatching
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SuspendRunCatchingTest {

    @Test
    fun `suspendRunCatching with success returns Result success`() = runTest {
        // GIVEN a block that returns a value
        val expected = "success"

        // WHEN we call suspendRunCatching
        val result = suspendRunCatching {
            expected
        }

        // THEN it returns success with expected value
        assertTrue(result.isSuccess)
        assertEquals(
            expected = expected,
            actual = result.getOrNull(),
        )
    }

    @Test
    fun `suspendRunCatching with regular exception returns Result failure`() = runTest {
        // GIVEN a block that throws a regular exception
        val exception = RuntimeException("error")

        // WHEN we call suspendRunCatching
        val result = suspendRunCatching {
            throw exception
        }

        // THEN it returns failure with same exception
        assertTrue(result.isFailure)
        assertEquals(
            expected = exception,
            actual = result.exceptionOrNull(),
        )
    }

    @Test
    fun `suspendRunCatching with CancellationException rethrows exception`() = runTest {
        // GIVEN a block that throws CancellationException
        val exception = CancellationException("cancelled")

        // WHEN / THEN it rethrows the exception instead of catching it
        assertFailsWith<CancellationException> {
            suspendRunCatching {
                throw exception
            }
        }
    }
}
