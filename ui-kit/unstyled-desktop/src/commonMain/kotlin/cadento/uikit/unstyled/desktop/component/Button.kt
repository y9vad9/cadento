package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.ButtonSize
import cadento.uikit.component.ButtonStyle
import cadento.uikit.component.ButtonVariant
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val CornerRadius = 6.dp
private val ButtonHeightSmall = 32.dp
private val ButtonHeightMedium = 40.dp
private val ButtonHeightLarge = 48.dp
private val ButtonHorizontalPadding = 16.dp
private val ButtonIconSpacing = 8.dp
private val ButtonBorderWidth = 1.dp

/**
 * Unstyled Desktop implementation of [ButtonStyle].
 */
public class UnstyledDesktopButtonStyle(private val colors: UnstyledDesktopColors) : ButtonStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        enabled: Boolean,
        variant: ButtonVariant,
        size: ButtonSize,
        onClick: () -> Unit,
        leadingIcon: (@Composable () -> Unit)?,
        trailingIcon: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        val isPressed by interactionSource.collectIsPressedAsState()

        val backgroundColor = when (variant) {
            ButtonVariant.Primary -> {
                if (isPressed) colors.primary.copy(alpha = 0.8f) 
                else if (isHovered) colors.primary.copy(alpha = 0.9f) 
                else colors.primary
            }
            ButtonVariant.Secondary -> {
                if (isPressed) colors.border 
                else if (isHovered) colors.border.copy(alpha = 0.5f) 
                else Color.Transparent
            }
            ButtonVariant.Danger -> {
                if (isPressed) colors.error.copy(alpha = 0.8f) 
                else if (isHovered) colors.error.copy(alpha = 0.9f) 
                else colors.error
            }
        }

        val contentColor = when (variant) {
            ButtonVariant.Primary, ButtonVariant.Danger -> colors.onPrimary
            ButtonVariant.Secondary -> colors.onBackground
        }

        val border = if (variant == ButtonVariant.Secondary) BorderStroke(ButtonBorderWidth, colors.border) else null

        val height = when (size) {
            ButtonSize.Small -> ButtonHeightSmall
            ButtonSize.Medium -> ButtonHeightMedium
            ButtonSize.Large -> ButtonHeightLarge
        }

        Box(
            modifier = modifier
                .height(height)
                .clip(RoundedCornerShape(CornerRadius))
                .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
                .then(if (border != null) Modifier.border(border, RoundedCornerShape(CornerRadius)) else Modifier)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, 
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(horizontal = ButtonHorizontalPadding),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ButtonIconSpacing)
                ) {
                    leadingIcon?.let { it() }
                    content()
                    trailingIcon?.let { it() }
                }
            }
        }
    }
}
