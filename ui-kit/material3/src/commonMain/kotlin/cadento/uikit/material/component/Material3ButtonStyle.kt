package cadento.uikit.material.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor as M3LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.ButtonSize
import cadento.uikit.component.ButtonStyle
import cadento.uikit.component.ButtonVariant
import androidx.compose.material3.Button as M3Button

private val ButtonHeightSmall = 32.dp
private val ButtonWidthSmall = 96.dp
private val ButtonHeightMedium = 40.dp
private val ButtonWidthMedium = 128.dp
private val ButtonHeightLarge = 48.dp
private val ButtonWidthLarge = 160.dp
private val ButtonArrangementSpacing = 8.dp

/**
 * Material3 implementation of [ButtonStyle] for
 * Compose Multiplatform using androidx.compose.material3.
 */
public class Material3ButtonStyle : ButtonStyle {

    @Composable
    override fun render(
        modifier: Modifier,
        enabled: Boolean,
        variant: ButtonVariant,
        size: ButtonSize,
        onClick: () -> Unit,
        leadingIcon: (@Composable (() -> Unit))?,
        trailingIcon: (@Composable (() -> Unit))?,
        content: @Composable () -> Unit
    ) {
        val containerColor = when (variant) {
            ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
            ButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
            ButtonVariant.Danger -> MaterialTheme.colorScheme.error
        }
        val contentColor = when (variant) {
            ButtonVariant.Primary -> MaterialTheme.colorScheme.onPrimary
            ButtonVariant.Secondary -> MaterialTheme.colorScheme.onSecondary
            ButtonVariant.Danger -> MaterialTheme.colorScheme.onError
        }

        val colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )

        val buttonModifier = modifier.then(
            when (size) {
                ButtonSize.Small -> Modifier.size(height = ButtonHeightSmall, width = ButtonWidthSmall)
                ButtonSize.Medium -> Modifier.size(height = ButtonHeightMedium, width = ButtonWidthMedium)
                ButtonSize.Large -> Modifier.size(height = ButtonHeightLarge, width = ButtonWidthLarge)
            }
        )

        M3Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            colors = colors,
            elevation = ButtonDefaults.buttonElevation(),
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                M3LocalContentColor provides contentColor
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(ButtonArrangementSpacing)) {
                    if (leadingIcon != null) leadingIcon()
                    content()
                    if (trailingIcon != null) trailingIcon()
                }
            }
        }
    }
}
