package cadento.tasks.domain

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Instant

object TaskDuePolicy {

    data class DateRanges(
        val today: ClosedRange<Instant>,
        val nextDay: ClosedRange<Instant>,
        val laterInWeek: ClosedRange<Instant>,
        val nextWeek: ClosedRange<Instant>,
    )

    /**
     * Calculates date ranges for soon due tasks based on current time and time zone.
     */
    fun calculateDateRanges(now: Instant, timeZone: TimeZone): DateRanges {
        val currentLocalDateTime = now.toLocalDateTime(timeZone)
        val currentDate = currentLocalDateTime.date

        val nextDayDate = currentDate.plus(DatePeriod(days = 1))
        val endOfToday = nextDayDate.atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val todayRange = now..endOfToday

        val nextDayStart = nextDayDate.atTime(0, 0).toInstant(timeZone)
        val endOfNextDay = nextDayDate.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val nextDayRange = nextDayStart..endOfNextDay

        val endOfCurrentWeekDate = currentDate.plus(
            DatePeriod(days = DayOfWeek.SUNDAY.ordinal - currentDate.dayOfWeek.ordinal),
        )
        val dayAfterNextDate = nextDayDate.plus(DatePeriod(days = 1))
        
        val laterInWeekStart = dayAfterNextDate.atTime(0, 0).toInstant(timeZone)
        val laterInWeekEnd = endOfCurrentWeekDate.plus(DatePeriod(days = 1))
            .atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val laterInWeekRange = laterInWeekStart..laterInWeekEnd

        val nextMonday = endOfCurrentWeekDate.plus(DatePeriod(days = 1))
        val nextSunday = nextMonday.plus(DatePeriod(days = 6))
        val nextWeekStart = nextMonday.atTime(0, 0).toInstant(timeZone)
        val nextWeekEnd = nextSunday.plus(DatePeriod(days = 1)).atTime(0, 0).toInstant(timeZone) - 1.nanoseconds
        val nextWeekRange = nextWeekStart..nextWeekEnd

        return DateRanges(
            today = todayRange,
            nextDay = nextDayRange,
            laterInWeek = laterInWeekRange,
            nextWeek = nextWeekRange,
        )
    }
}
