package timemate.coroutines

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

/**
 * A safe version of [runCatching] for suspend blocks.
 *
 * It re-throws [CancellationException] to ensure that coroutine cancellation
 * is handled correctly by the structured concurrency.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <R> suspendRunCatching(block: () -> R): Result<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
