package cadento.uikit.material.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text as M3Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import cadento.uikit.LocalContentColor
import cadento.uikit.component.TextLevel
import cadento.uikit.component.TextStyle

public class Material3TextStyle : TextStyle {
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
        val style = when (level) {
            TextLevel.Display -> MaterialTheme.typography.displayLarge
            TextLevel.Title -> MaterialTheme.typography.titleLarge
            TextLevel.Headline -> MaterialTheme.typography.headlineMedium
            TextLevel.Body -> MaterialTheme.typography.bodyMedium
            TextLevel.Label -> MaterialTheme.typography.labelMedium
            TextLevel.Caption -> MaterialTheme.typography.bodySmall
        }

        M3Text(
            text = text,
            modifier = modifier,
            style = style,
            color = if (inheritedColor != Color.Unspecified) inheritedColor else Color.Unspecified,
            textAlign = align,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}
