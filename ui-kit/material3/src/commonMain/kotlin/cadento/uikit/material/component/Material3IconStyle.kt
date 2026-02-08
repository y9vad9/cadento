package cadento.uikit.material.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import cadento.uikit.component.IconStyle
import androidx.compose.material3.Icon as M3Icon
import cadento.uikit.LocalContentColor

/**
 * Material 3 implementation of [IconStyle].
 */
public class Material3IconStyle : IconStyle {
    @Composable
    override fun render(
        painter: Painter,
        contentDescription: String?,
        modifier: Modifier,
        tint: Color,
    ) {
        M3Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}
