package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cadento.uikit.component.CardStyle
import cadento.uikit.component.CardVariant
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val CornerRadius = 6.dp
private val CardBorderWidth = 1.dp
private val CardPadding = 16.dp

/**
 * Unstyled Desktop implementation of [CardStyle].
 */
public class UnstyledDesktopCardStyle(private val colors: UnstyledDesktopColors) : CardStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        onClick: (() -> Unit)?,
        variant: CardVariant,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    ) {
        val border = when (variant) {
            CardVariant.Primary -> null
            CardVariant.Secondary -> colors.border
            CardVariant.Tertiary -> null
        }
        val backgroundColor = when (variant) {
            CardVariant.Primary -> colors.surface
            CardVariant.Secondary -> colors.surface
            CardVariant.Tertiary -> colors.background
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(CornerRadius))
                .background(backgroundColor)
                .then(
                    if (border != null) Modifier.border(
                        CardBorderWidth,
                        border,
                        RoundedCornerShape(CornerRadius)
                    ) else Modifier
                )
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(CardPadding)
        ) {
            Column {
                header?.let { it() }
                content()
                footer?.let { it() }
            }
        }
    }
}
