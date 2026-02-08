package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware card component.
 *
 * @param modifier Modifier to apply to the card
 * @param onClick Optional click callback. If provided, the card becomes interactive.
 * @param variant Visual variant
 * @param header Optional header slot
 * @param footer Optional footer slot
 * @param content Main content slot
 */
@Composable
public fun Card(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: CardVariant = CardVariant.Primary,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val style = LocalCardStyle.current
    style.render(
        modifier = modifier,
        onClick = onClick,
        variant = variant,
        header = header,
        footer = footer,
        content = content
    )
}

/**
 * Defines the visual emphasis of a [Card].
 */
public enum class CardVariant {
    /** High emphasis card, typically used for primary featured content. */
    Primary,
    /** Medium emphasis card, used for standard grouped content. */
    Secondary,
    /** Low emphasis card, used for content that should blend with the background. */
    Tertiary
}

/**
 * Strategy for rendering a [Card] based on the current theme.
 */
public interface CardStyle {
    /**
     * Renders a card with the given slots.
     *
     * @param modifier Modifier to be applied to the outer container.
     * @param onClick If provided, the card will be interactive and clickable.
     * @param variant The semantic emphasis of the card.
     * @param header Optional content to be placed at the top of the card.
     * @param footer Optional content to be placed at the bottom of the card.
     * @param content The main body of the card.
     */
    @Composable
    public fun render(
        modifier: Modifier,
        onClick: (() -> Unit)?,
        variant: CardVariant,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    )
}

public val LocalCardStyle: ProvidableCompositionLocal<CardStyle> = staticCompositionLocalOf {
    error("No CardStyle provided")
}
