package timemate.time

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone

public interface SystemTimeZoneProvider {
    public val timeZone: StateFlow<TimeZone>
}
