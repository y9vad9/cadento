package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware button consisting of a single icon.
 *
 * @param onClick Click callback.
 * @param icon The icon to display.
 * @param contentDescription Text used by accessibility services.
 * @param modifier Layout modifier.
 * @param enabled Controls interactivity.
 */
@Composable
public fun IconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style = LocalIconButtonStyle.current
    style.render(
        onClick = onClick,
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * Strategy for rendering an [IconButton].
 */
public interface IconButtonStyle {
    @Composable
    public fun render(
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        contentDescription: String?,
        modifier: Modifier,
        enabled: Boolean,
    )
}

public val LocalIconButtonStyle: ProvidableCompositionLocal<IconButtonStyle> = staticCompositionLocalOf {
    error("No IconButtonStyle provided")
}
