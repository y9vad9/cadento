package cadento.uikit.material.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cadento.uikit.component.ButtonSize
import cadento.uikit.component.ButtonStyle
import cadento.uikit.component.ButtonVariant
import androidx.compose.material3.Button as M3Button

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
        val colors = when (variant) {
            ButtonVariant.Primary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
            ButtonVariant.Danger -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        }

        val buttonModifier = modifier.then(
            when (size) {
                ButtonSize.Small -> Modifier.size(height = 32.dp, width = 96.dp)
                ButtonSize.Medium -> Modifier.size(height = 40.dp, width = 128.dp)
                ButtonSize.Large -> Modifier.size(height = 48.dp, width = 160.dp)
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (leadingIcon != null) leadingIcon()
                content()
                if (trailingIcon != null) trailingIcon()
            }
        }
    }
}
