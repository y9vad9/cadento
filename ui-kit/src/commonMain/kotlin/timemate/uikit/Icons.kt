package timemate.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
public interface Icons {
    public val add: ImageVector
    public val delete: ImageVector

    public companion object {
        public val current: Icons @Composable get() = LocalIcons.current
    }
}

public val LocalIcons: CompositionLocal<Icons> = staticCompositionLocalOf {
    error("No icons provided.")
}
