package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.TextLevel
import cadento.uikit.component.TextStyle
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors
import androidx.compose.ui.text.TextStyle as ComposeTextStyle

/**
 * Unstyled Desktop implementation of [TextStyle].
 */
public class UnstyledDesktopTextStyle(private val colors: UnstyledDesktopColors) : TextStyle {
    @Composable
    override fun render(
        text: String,
        modifier: Modifier,
        level: TextLevel,
        align: TextAlign,
        overflow: TextOverflow,
        maxLines: Int
    ) {
        val inheritedColor = LocalContentColor.current
        val defaultColor = when (level) {
            TextLevel.Display, TextLevel.Title, TextLevel.Headline, TextLevel.Body -> colors.onBackground
            TextLevel.Label, TextLevel.Caption -> colors.onHeader
        }
        
        val color = if (inheritedColor != Color.Unspecified) inheritedColor else defaultColor

        val style = when (level) {
            TextLevel.Display -> ComposeTextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold)
            TextLevel.Title -> ComposeTextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            TextLevel.Headline -> ComposeTextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            TextLevel.Body -> ComposeTextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
            TextLevel.Label -> ComposeTextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
            TextLevel.Caption -> ComposeTextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
        }.copy(color = color, textAlign = align)

        BasicText(
            text = text,
            modifier = modifier,
            style = style,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}
