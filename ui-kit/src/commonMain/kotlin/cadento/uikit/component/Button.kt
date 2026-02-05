package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

// --------------------
// Core API
// --------------------

/**
 * A theme-aware button component.
 *
 * @param onClick Click callback
 * @param modifier Modifier to apply to the button
 * @param enabled Whether the button is enabled
 * @param variant Visual variant
 * @param size Button size
 * @param leadingIcon Optional leading icon slot
 * @param trailingIcon Optional trailing icon slot
 * @param content Composable content (usually text)
 */
@Composable
public fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit
) {
    val style = LocalButtonStyle.current
    style.render(
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        size = size,
        onClick = onClick,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        content = content
    )
}

/**
 * Primary button.
 */
@Composable
public fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = ButtonVariant.Primary,
        size = size,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        content = content
    )
}

/**
 * Secondary button.
 */
@Composable
public fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = ButtonVariant.Secondary,
        size = size,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        content = content
    )
}

/**
 * Danger button.
 */
@Composable
public fun DangerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = ButtonVariant.Danger,
        size = size,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        content = content
    )
}

// --------------------
// Variants & Sizes
// --------------------

/**
 * Defines the visual variant of a [Button].
 */
public enum class ButtonVariant {
    Primary,
    Secondary,
    Danger
}

/**
 * Defines the size of a [Button].
 */
public enum class ButtonSize {
    Small,
    Medium,
    Large
}

// --------------------
// Theming
// --------------------

/**
 * Theme-specific rendering strategy for [Button].
 */
public interface ButtonStyle {

    /**
     * Renders a button with the given parameters.
     *
     * @param modifier Modifier to apply to the button
     * @param enabled Whether the button is enabled
     * @param variant The visual variant
     * @param size The size of the button
     * @param onClick Click callback
     * @param leadingIcon Optional leading icon
     * @param trailingIcon Optional trailing icon
     * @param content Composable content
     */
    @Composable
    public fun render(
        modifier: Modifier,
        enabled: Boolean,
        variant: ButtonVariant,
        size: ButtonSize,
        onClick: () -> Unit,
        leadingIcon: (@Composable (() -> Unit))? = null,
        trailingIcon: (@Composable (() -> Unit))? = null,
        content: @Composable () -> Unit,
    )
}

/**
 * Provides the current [ButtonStyle] for theming.
 */
public val LocalButtonStyle: ProvidableCompositionLocal<ButtonStyle> = staticCompositionLocalOf {
    error("No ButtonStyle provided")
}
