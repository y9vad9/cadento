package cadento.uikit.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Clock

public val LocalClock: ProvidableCompositionLocal<Clock> = staticCompositionLocalOf {
    Clock.System
}
