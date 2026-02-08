package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware badge component.
 *
 * @param modifier Layout modifier.
 * @param variant Visual variant (Dot, Count, or Label).
 * @param intent Semantic purpose of the badge (determines color).
 * @param content Optional content to display inside the badge (e.g. number or text).
 * @param container Optional content that this badge is attached to (anchored).
 */
@Composable
public fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Dot,
    intent: BadgeIntent = BadgeIntent.Default,
    content: (@Composable () -> Unit)? = null,
    container: (@Composable () -> Unit)? = null,
) {
    val style = LocalBadgeStyle.current
    style.render(
        modifier = modifier,
        variant = variant,
        intent = intent,
        content = content,
        container = container
    )
}

public enum class BadgeVariant {
    /** A small circle without content. */
    Dot,
    /** A circle/pill typically containing a number. */
    Count,
    /** A rectangular/pill shape containing text (Github-style). */
    Label,
    /** A badge with a visible border. */
    Outlined
}

/**
 * Defines the semantic purpose and color scheme of a [Badge].
 */
public enum class BadgeIntent {
    Default,
    Primary,
    Secondary,
    Success,
    Error,
    Warning,
    Info
}

/**
 * Strategy for rendering a [Badge] based on the current theme.
 */
public interface BadgeStyle {
    /**
     * Renders a badge with the given parameters.
     */
    @Composable
    public fun render(
        modifier: Modifier,
        variant: BadgeVariant,
        intent: BadgeIntent,
        content: (@Composable () -> Unit)?,
        container: (@Composable () -> Unit)?
    )
}

public val LocalBadgeStyle: ProvidableCompositionLocal<BadgeStyle> = staticCompositionLocalOf {
    error("No BadgeStyle provided")
}