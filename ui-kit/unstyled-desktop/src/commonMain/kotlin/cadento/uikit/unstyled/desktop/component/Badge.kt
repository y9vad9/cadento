package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.BadgeIntent
import cadento.uikit.component.BadgeStyle
import cadento.uikit.component.BadgeVariant
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val BadgeBorderWidth = 1.dp
private val BadgeCornerRadius = 4.dp
private val BadgeHorizontalPadding = 6.dp
private val BadgeVerticalPadding = 2.dp
private val BadgeContainerOffset = 4.dp

/**
 * Unstyled Desktop implementation of [BadgeStyle].
 */
public class UnstyledDesktopBadgeStyle(private val colors: UnstyledDesktopColors) : BadgeStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        variant: BadgeVariant,
        intent: BadgeIntent,
        content: (@Composable () -> Unit)?,
        container: (@Composable () -> Unit)?
    ) {
        val backgroundColor = when (intent) {
            BadgeIntent.Primary -> colors.accent
            BadgeIntent.Secondary -> colors.secondary
            BadgeIntent.Success -> colors.success
            BadgeIntent.Error -> colors.error
            BadgeIntent.Warning -> colors.warning
            BadgeIntent.Info -> colors.info
            BadgeIntent.Default -> colors.onBackground
        }
        
        val onBackgroundColor = when (intent) {
            BadgeIntent.Primary -> colors.onAccent
            BadgeIntent.Secondary -> colors.onSecondary
            BadgeIntent.Success -> colors.onSuccess
            BadgeIntent.Error -> colors.onError
            BadgeIntent.Warning -> colors.onWarning
            BadgeIntent.Info -> colors.onInfo
            BadgeIntent.Default -> colors.background
        }

        val badge: @Composable () -> Unit = {
            val badgeModifier = modifier
                .then(
                    if (variant == BadgeVariant.Outlined) {
                        Modifier.border(BadgeBorderWidth, backgroundColor, CircleShape)
                    } else {
                        Modifier.background(
                            backgroundColor,
                            if (variant == BadgeVariant.Dot) CircleShape else RoundedCornerShape(BadgeCornerRadius)
                        )
                    }
                )
                .padding(
                    horizontal = if (variant == BadgeVariant.Dot) 0.dp else BadgeHorizontalPadding, 
                    vertical = if (variant == BadgeVariant.Dot) 0.dp else BadgeVerticalPadding
                )

            Box(
                modifier = badgeModifier,
                contentAlignment = Alignment.Center
            ) {
                val contentColor = if (variant == BadgeVariant.Outlined) backgroundColor else onBackgroundColor
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content?.let { it() }
                }
            }
        }

        if (container != null) {
            Box(contentAlignment = Alignment.TopEnd) {
                container()
                Box(modifier = Modifier.offset(x = BadgeContainerOffset, y = -BadgeContainerOffset)) {
                    badge()
                }
            }
        } else {
            badge()
        }
    }
}
