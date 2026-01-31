package timemate.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
public data class UiColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val surface: Color,
    val background: Color,
    val accent: Color,
    val border: Color,
) {
    public companion object {
        public val current: UiColors @Composable get() = LocalUiColors.current
    }
}

public val LocalUiColors: CompositionLocal<UiColors> = staticCompositionLocalOf {
    error("No colors provided")
}
