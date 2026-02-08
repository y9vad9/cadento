package cadento.uikit.material.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge as M3Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.LocalContentColor as M3LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.LocalUiColors
import cadento.uikit.component.BadgeIntent
import cadento.uikit.component.BadgeStyle
import cadento.uikit.component.BadgeVariant

private val BadgeLabelHorizontalPadding = 4.dp
private val BadgeBorderWidth = 1.dp
private val BadgeStandaloneHorizontalPadding = 8.dp
private val BadgeStandaloneVerticalPadding = 2.dp

/**
 * Material 3 implementation of [BadgeStyle].
 */
public class Material3BadgeStyle : BadgeStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        variant: BadgeVariant,
        intent: BadgeIntent,
        content: (@Composable () -> Unit)?,
        container: (@Composable () -> Unit)?
    ) {
        val uiColors = LocalUiColors.current
        val backgroundColor = when (intent) {
            BadgeIntent.Default -> uiColors.surface
            BadgeIntent.Primary -> uiColors.accent
            BadgeIntent.Secondary -> uiColors.textSecondary
            BadgeIntent.Success -> uiColors.success
            BadgeIntent.Error -> uiColors.error
            BadgeIntent.Warning -> uiColors.warning
            BadgeIntent.Info -> uiColors.info
        }
        val onBackgroundColor = when (intent) {
            BadgeIntent.Default -> uiColors.textPrimary
            BadgeIntent.Primary -> uiColors.onAccent
            BadgeIntent.Secondary -> uiColors.background
            BadgeIntent.Success -> uiColors.onSuccess
            BadgeIntent.Error -> uiColors.onError
            BadgeIntent.Warning -> uiColors.onWarning
            BadgeIntent.Info -> uiColors.onInfo
        }

        val badge: @Composable (Dp, Dp) -> Unit = { hPadding, vPadding ->
            when (variant) {
                BadgeVariant.Dot -> M3Badge(
                    modifier = modifier,
                    containerColor = backgroundColor
                )
                BadgeVariant.Count -> M3Badge(
                    modifier = modifier,
                    containerColor = backgroundColor,
                    contentColor = onBackgroundColor
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides onBackgroundColor,
                        M3LocalContentColor provides onBackgroundColor
                    ) {
                        content?.let { it() }
                    }
                }
                BadgeVariant.Label, BadgeVariant.Outlined -> {
                    BadgeContent(
                        modifier = modifier,
                        variant = variant,
                        backgroundColor = backgroundColor,
                        onBackgroundColor = onBackgroundColor,
                        horizontalPadding = hPadding,
                        verticalPadding = vPadding,
                        content = content
                    )
                }
            }
        }

        if (container != null) {
            BadgedBox(
                badge = { badge(BadgeLabelHorizontalPadding, 0.dp) }
            ) {
                container()
            }
        } else {
            val horizontalPadding = if (variant == BadgeVariant.Label || variant == BadgeVariant.Outlined) {
                BadgeStandaloneHorizontalPadding
            } else {
                BadgeLabelHorizontalPadding
            }
            val verticalPadding = if (variant == BadgeVariant.Label || variant == BadgeVariant.Outlined) {
                BadgeStandaloneVerticalPadding
            } else {
                0.dp
            }
            badge(horizontalPadding, verticalPadding)
        }
    }
}

@Composable
private fun BadgeContent(
    modifier: Modifier,
    variant: BadgeVariant,
    backgroundColor: Color,
    onBackgroundColor: Color,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    content: (@Composable () -> Unit)?
) {
    val isOutlined = variant == BadgeVariant.Outlined
    val contentColor = if (isOutlined) backgroundColor else onBackgroundColor

    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (isOutlined) {
                    Modifier.border(BadgeBorderWidth, backgroundColor, CircleShape)
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            M3LocalContentColor provides contentColor
        ) {
            content?.let { it() }
        }
    }
}