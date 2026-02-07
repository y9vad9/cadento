package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware text input component.
 *
 * @param value Current text value
 * @param onValueChange Callback for text changes
 * @param modifier Modifier to apply
 * @param label Optional label
 * @param placeholder Optional placeholder
 * @param supportingText Optional supporting/helper text
 * @param error Optional error text. If provided, the input enters an error state.
 * @param leadingIcon Optional leading icon slot
 * @param trailingIcon Optional trailing icon slot
 * @param isEnabled Whether the input is enabled
 * @param isReadOnly Whether the input is read-only
 * @param isMultiline Whether the input supports multiple lines
 * @param maxLines Maximum height of the field in lines
 */
@Composable
public fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    error: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    isMultiline: Boolean = false,
    maxLines: Int = if (isMultiline) Int.MAX_VALUE else 1,
) {
    val style = LocalTextInputStyle.current
    style.render(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        error = error,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isEnabled = isEnabled,
        isReadOnly = isReadOnly,
        isMultiline = isMultiline,
        maxLines = maxLines
    )
}

/**
 * Theme-aware rendering strategy for [TextInput].
 */
public interface TextInputStyle {
    /**
     * Renders a text input with the given parameters.
     *
     * @param value Current input value.
     * @param onValueChange Callback for character changes.
     * @param modifier Modifier for the outer container.
     * @param label Descriptive label shown above or inside the field.
     * @param placeholder Hint text shown when the field is empty.
     * @param supportingText Helper text shown below the field.
     * @param error Error message. If non-null, the field should show an error state.
     * @param leadingIcon Icon shown at the start of the field.
     * @param trailingIcon Icon shown at the end of the field.
     * @param isEnabled Controls whether the field is interactive.
     * @param isReadOnly Controls whether the text can be modified.
     * @param isMultiline Whether the field supports multiple lines.
     * @param maxLines Maximum height of the field in lines.
     */
    @Composable
    public fun render(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        label: String?,
        placeholder: String?,
        supportingText: String?,
        error: String?,
        leadingIcon: (@Composable () -> Unit)?,
        trailingIcon: (@Composable () -> Unit)?,
        isEnabled: Boolean,
        isReadOnly: Boolean,
        isMultiline: Boolean,
        maxLines: Int,
    )
}

public val LocalTextInputStyle: ProvidableCompositionLocal<TextInputStyle> = staticCompositionLocalOf {
    error("No TextInputStyle provided")
}