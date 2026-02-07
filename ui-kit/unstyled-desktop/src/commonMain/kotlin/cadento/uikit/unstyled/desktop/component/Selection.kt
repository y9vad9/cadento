package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cadento.uikit.component.CheckboxStyle
import cadento.uikit.component.SwitchStyle
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val CheckboxSize = 18.dp
private val CheckboxBorderWidth = 1.dp
private val CheckboxCornerRadius = 4.dp
private val CheckmarkSize = 10.dp
private val CheckmarkStrokeWidth = 1.dp

private val SwitchWidth = 36.dp
private val SwitchHeight = 20.dp
private val SwitchCornerRadius = 10.dp
private val SwitchPadding = 2.dp
private val SwitchThumbSize = 16.dp

/**
 * Unstyled Desktop implementation of [CheckboxStyle].
 */
public class UnstyledDesktopCheckboxStyle(private val colors: UnstyledDesktopColors) : CheckboxStyle {
    @Composable
    override fun render(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, enabled: Boolean) {
        Box(
            modifier = modifier
                .size(CheckboxSize)
                .border(
                    CheckboxBorderWidth,
                    if (checked) colors.accent else colors.border,
                    RoundedCornerShape(CheckboxCornerRadius)
                )
                .background(
                    if (checked) colors.accent else Color.Transparent,
                    RoundedCornerShape(CheckboxCornerRadius)
                )
                .clickable(enabled = enabled) { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(modifier = Modifier.size(CheckmarkSize)) {
                    val strokeWidth = CheckmarkStrokeWidth.toPx()
                    drawLine(
                        color = colors.onAccent,
                        start = Offset(size.width * 0.2f, size.height * 0.5f),
                        end = Offset(size.width * 0.4f, size.height * 0.7f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = colors.onAccent,
                        start = Offset(size.width * 0.4f, size.height * 0.7f),
                        end = Offset(size.width * 0.8f, size.height * 0.3f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * Unstyled Desktop implementation of [SwitchStyle].
 */
public class UnstyledDesktopSwitchStyle(private val colors: UnstyledDesktopColors) : SwitchStyle {
    @Composable
    override fun render(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, enabled: Boolean) {
        Box(
            modifier = modifier
                .size(width = SwitchWidth, height = SwitchHeight)
                .background(if (checked) colors.accent else colors.border, RoundedCornerShape(SwitchCornerRadius))
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .padding(SwitchPadding)
        ) {
            Box(
                modifier = Modifier
                    .size(SwitchThumbSize)
                    .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(colors.onAccent, CircleShape)
            )
        }
    }
}