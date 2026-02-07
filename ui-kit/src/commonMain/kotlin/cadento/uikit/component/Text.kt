package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * A theme-aware text component using semantic typography levels.
 */
@Composable
public fun Text(
    text: String,
    modifier: Modifier = Modifier,
    level: TextLevel = TextLevel.Body,
    align: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    val style = LocalTextStyle.current
    style.render(
        text = text,
        modifier = modifier,
        level = level,
        align = align,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * Semantic typography levels.
 */
public enum class TextLevel {
    /** Extra large text for hero sections or primary displays. */
    Display,
    /** Large text for screen titles. */
    Title,
    /** Medium-large text for section headers. */
    Headline,
    /** Standard text for primary reading content. */
    Body,
    /** Small text for UI labels or secondary info. */
    Label,
    /** Extra small text for fine print or captions. */
    Caption
}

/**
 * Strategy for rendering text based on the theme.
 */
public interface TextStyle {
    /**
     * @param text Content to be displayed.
     * @param modifier Layout modifier.
     * @param level Semantic typography level.
     * @param align Horizontal alignment.
     * @param overflow How to handle text overflow.
     * @param maxLines Maximum number of lines.
     */
    @Composable
    public fun render(
        text: String,
        modifier: Modifier,
        level: TextLevel,
        align: TextAlign,
        overflow: TextOverflow,
        maxLines: Int
    )
}

public val LocalTextStyle: ProvidableCompositionLocal<TextStyle> = staticCompositionLocalOf {
    error("No TextStyle provided")
}
