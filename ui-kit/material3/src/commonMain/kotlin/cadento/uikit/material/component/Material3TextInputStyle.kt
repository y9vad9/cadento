package cadento.uikit.material.component

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.TextInputStyle

public class Material3TextInputStyle : TextInputStyle {
    @Composable
    override fun render(
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
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            supportingText = (error ?: supportingText)?.let { { Text(it) } },
            isError = error != null,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = isEnabled,
            readOnly = isReadOnly,
            singleLine = !isMultiline,
            maxLines = maxLines
        )
    }
}
