package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cadento.uikit.component.IconStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cadento.uikit.LocalContentColor
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.Modifier.Companion

/**
 * Unstyled Desktop implementation of [IconStyle].
 */
public class UnstyledDesktopIconStyle : IconStyle {
    @Composable
    override fun render(
        painter: Painter,
        contentDescription: String?,
        modifier: Modifier,
        tint: Color,
    ) {
        val color = if (tint == Color.Unspecified) LocalContentColor.current else tint
        val colorFilter =
            remember(color) { if (color == Color.Unspecified) null else ColorFilter.tint(color) }
        val semantics =
            if (contentDescription != null) {
                Modifier.semantics {
                    this.contentDescription = contentDescription
                    this.role = Role.Image
                }
            } else {
                Modifier
            }

        val defaultSize = 24.dp // Default icon size for unstyled desktop

        val defaultModifier = with(LocalDensity.current) {
            Modifier
                .sizeIn(minWidth = defaultSize, minHeight = defaultSize)
                .wrapContentSize(Alignment.Center)
        }

        Box(
            modifier
                .then(defaultModifier)
                .paint(painter, colorFilter = colorFilter, contentScale = ContentScale.Fit)
                .then(semantics)
        )
    }
}
