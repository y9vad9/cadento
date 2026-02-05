package cadento.tasks.application

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone

interface TimeZoneProvider {
    val timeZone: StateFlow<TimeZone>
}
