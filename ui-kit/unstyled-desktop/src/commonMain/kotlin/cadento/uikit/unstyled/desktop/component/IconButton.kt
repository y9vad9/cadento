package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.IconButtonStyle
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val IconButtonSize = 36.dp
private val IconButtonCornerRadius = 6.dp

/**
 * Unstyled Desktop implementation of [IconButtonStyle].
 */
public class UnstyledDesktopIconButtonStyle(private val colors: UnstyledDesktopColors) : IconButtonStyle {
    @Composable
    override fun render(onClick: () -> Unit, icon: @Composable () -> Unit, contentDescription: String?, modifier: Modifier, enabled: Boolean) {
        Box(
            modifier = modifier
                .size(IconButtonSize)
                .clip(RoundedCornerShape(IconButtonCornerRadius))
                .clickable(onClick = onClick, enabled = enabled),
            contentAlignment = Alignment.Center
        ) {
            val iconColor = if (enabled) colors.onBackground else colors.onBackground.copy(alpha = 0.5f)
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                icon()
            }
        }
    }
}
