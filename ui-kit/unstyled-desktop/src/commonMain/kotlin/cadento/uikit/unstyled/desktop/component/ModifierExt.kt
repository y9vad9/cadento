package cadento.uikit.unstyled.desktop.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a 1-pixel border on specific sides of the component.
 * 
 * @param color The color of the border.
 * @param start Thickness of the start border.
 * @param end Thickness of the end border.
 * @param top Thickness of the top border.
 * @param bottom Thickness of the bottom border.
 */
public fun Modifier.desktopBorder(
    color: Color,
    start: Dp = 0.dp,
    end: Dp = 0.dp,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp
): Modifier = this.drawBehind {
    val strokeWidth = 1.dp.toPx()
    
    if (start > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidth
        )
    }
    if (end > 0.dp) {
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }
    if (top > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth
        )
    }
    if (bottom > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth
        )
    }
}
