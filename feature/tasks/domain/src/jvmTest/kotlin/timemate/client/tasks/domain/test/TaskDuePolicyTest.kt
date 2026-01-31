package timemate.client.tasks.domain.test

import kotlinx.datetime.TimeZone
import timemate.client.tasks.domain.TaskDuePolicy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class TaskDuePolicyTest {

    @Test
    fun `calculateDateRanges with Monday returns correct ranges`() {
        // GIVEN a Monday at 10:00 UTC
        val monday = Instant.parse("2024-01-01T10:00:00Z")
        val timeZone = TimeZone.UTC

        // WHEN we calculate date ranges
        val result = TaskDuePolicy.calculateDateRanges(monday, timeZone)

        // THEN ranges should be correct
        // Today: Monday 10:00 to Monday 23:59:59.999999999
        assertEquals(
            expected = monday,
            actual = result.today.start,
        )
        assertEquals(
            expected = Instant.parse("2024-01-01T23:59:59.999999999Z"),
            actual = result.today.endInclusive,
        )

        // Next Day: Tuesday 00:00 to Tuesday 23:59:59.999999999
        assertEquals(
            expected = Instant.parse("2024-01-02T00:00:00Z"),
            actual = result.nextDay.start,
        )
        assertEquals(
            expected = Instant.parse("2024-01-02T23:59:59.999999999Z"),
            actual = result.nextDay.endInclusive,
        )

        // Later in Week: Wednesday 00:00 to Sunday 23:59:59.999999999
        assertEquals(
            expected = Instant.parse("2024-01-03T00:00:00Z"),
            actual = result.laterInWeek.start,
        )
        assertEquals(
            expected = Instant.parse("2024-01-07T23:59:59.999999999Z"),
            actual = result.laterInWeek.endInclusive,
        )

        // Next Week: Next Monday 00:00 to Next Sunday 23:59:59.999999999
        assertEquals(
            expected = Instant.parse("2024-01-08T00:00:00Z"),
            actual = result.nextWeek.start,
        )
        assertEquals(
            expected = Instant.parse("2024-01-14T23:59:59.999999999Z"),
            actual = result.nextWeek.endInclusive,
        )
    }

    @Test
    fun `calculateDateRanges with Saturday returns ranges with next week starting on Monday`() {
        // GIVEN a Saturday at 10:00 UTC
        val saturday = Instant.parse("2024-01-06T10:00:00Z")
        val timeZone = TimeZone.UTC

        // WHEN we calculate date ranges
        val result = TaskDuePolicy.calculateDateRanges(saturday, timeZone)

        // THEN ranges should be correct
        // Today: Saturday
        // Next Day: Sunday
        // Later in Week: (Monday+)
        // Logic for laterInWeek uses dayAfterNext (Sunday + 1 = Monday)
        // dayAfterNext = nextDayDate + 1 day = Sunday + 1 day = Monday
        // endOfWeekDate = Monday + (Sunday.ordinal - Monday.ordinal) = Sunday
        // So laterInWeek is Monday to Sunday.

        assertEquals(
            expected = Instant.parse("2024-01-08T00:00:00Z"),
            actual = result.laterInWeek.start,
        )
        assertEquals(
            expected = Instant.parse("2024-01-14T23:59:59.999999999Z"),
            actual = result.laterInWeek.endInclusive,
        )
    }
}
