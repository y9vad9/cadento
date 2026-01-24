package timemate.client.tasks.application

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

/**
 * Emits the current [LocalDateTime] in the given [TimeZone], updating at each local midnight
 * or whenever the [timeZoneFlow] emits a new [TimeZone].
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun Clock.localDateTimeFlow(timeZoneFlow: StateFlow<TimeZone>): Flow<LocalDateTime> =
    timeZoneFlow.flatMapLatest { tz ->
        flow {
            while (true) {
                val nowInstant = this@localDateTimeFlow.now()
                val nowLocal = nowInstant.toLocalDateTime(tz)
                emit(nowLocal)

                // calculate next local midnight
                val nextMidnightLocal = nowLocal.date.nextDay.atTime(0, 0)
                val nextMidnightInstant = nextMidnightLocal.toInstant(tz)
                val delayDuration = (nextMidnightInstant - nowInstant).coerceAtLeast(1.seconds)

                delay(delayDuration)
            }
        }
    }

private val LocalDate.nextDay: LocalDate
    get() = this.plus(kotlinx.datetime.DatePeriod(days = 1))
