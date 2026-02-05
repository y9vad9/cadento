package cadento.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import java.util.TimeZone as JTimeZone

/**
 * JVM implementation of [SystemTimeZoneProvider] that polls for system timezone changes.
 *
 * It periodically refreshes the default JVM timezone by clearing the internal cache
 * and updates the [timeZone] flow if a change is detected.
 */
public class JvmSystemTimeZoneProvider(
    scope: CoroutineScope,
    private val pollInterval: Duration = 1.minutes,
) : SystemTimeZoneProvider {
    private val _timeZone: MutableStateFlow<TimeZone> = MutableStateFlow(
        run {
            JTimeZone.setDefault(null)
            TimeZone.currentSystemDefault()
        },
    )
    override val timeZone: StateFlow<TimeZone> = _timeZone.asStateFlow()

    init {
        scope.launch {
            while (isActive) {
                delay(pollInterval)
                JTimeZone.setDefault(null)
                val current = TimeZone.currentSystemDefault()
                _timeZone.value = current
            }
        }
    }
}