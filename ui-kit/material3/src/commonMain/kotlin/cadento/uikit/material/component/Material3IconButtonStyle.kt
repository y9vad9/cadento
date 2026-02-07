package cadento.uikit.material.component

import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import cadento.uikit.component.IconButtonStyle

public class Material3IconButtonStyle : IconButtonStyle {
    @Composable
    override fun render(
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        contentDescription: String?,
        modifier: Modifier,
        enabled: Boolean
    ) {
        val buttonModifier = if (contentDescription != null) {
            modifier.semantics { this.contentDescription = contentDescription }
        } else {
            modifier
        }

        M3IconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled
        ) {
            icon()
        }
    }
}
