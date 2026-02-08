package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import cadento.uikit.LocalContentColor

/**
 * Renders an icon based on the current theme's [IconStyle].
 *
 * @param painter [Painter] to draw inside this icon.
 * @param contentDescription text used by accessibility services.
 * @param modifier the [Modifier] to be applied to this icon.
 * @param tint tint to be applied to [painter]. Defaults to [LocalContentColor.current].
 */
@Composable
public fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    LocalIconStyle.current.render(painter, contentDescription, modifier, tint)
}

/**
 * Strategy for rendering an [Icon] based on the current theme.
 */
public interface IconStyle {
    @Composable
    public fun render(
        painter: Painter,
        contentDescription: String?,
        modifier: Modifier,
        tint: Color,
    )
}

public val LocalIconStyle: ProvidableCompositionLocal<IconStyle> = staticCompositionLocalOf {
    error("No IconStyle provided")
}
