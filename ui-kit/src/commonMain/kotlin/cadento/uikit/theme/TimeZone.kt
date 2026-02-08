package cadento.uikit.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.datetime.TimeZone

/**
 * CompositionLocal for [TimeZone].
 * Defaults to the current system default timezone.
 */
public val LocalTimeZone: ProvidableCompositionLocal<TimeZone> = staticCompositionLocalOf {
    TimeZone.currentSystemDefault()
}
