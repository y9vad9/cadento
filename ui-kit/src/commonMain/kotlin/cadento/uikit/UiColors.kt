package cadento.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
public data class UiColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val surface: Color,
    val background: Color,
    val accent: Color,
    val onAccent: Color,
    val border: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val error: Color,
    val onError: Color,
    val info: Color,
    val onInfo: Color
) {
    public companion object {
        public val current: UiColors @Composable get() = LocalUiColors.current
    }
}

public val LocalUiColors: ProvidableCompositionLocal<UiColors> = staticCompositionLocalOf {
    error("No colors provided")
}

/**
 * CompositionLocal for the preferred color of content (text and icons).
 */
public val LocalContentColor: ProvidableCompositionLocal<Color> = staticCompositionLocalOf {
    Color.Unspecified
}
