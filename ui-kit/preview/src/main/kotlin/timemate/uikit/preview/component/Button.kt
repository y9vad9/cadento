package timemate.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import timemate.uikit.component.ButtonSize
import timemate.uikit.component.DangerButton
import timemate.uikit.component.PrimaryButton
import timemate.uikit.component.SecondaryButton
import timemate.uikit.component.LocalButtonStyle
import timemate.uikit.component.ButtonStyle
import timemate.uikit.material.component.Material3ButtonStyle
import timemate.uikit.icon.Icons
import timemate.uikit.material.icon.MaterialIcons
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Preview all button variants and sizes for all implementations using implementation-specific icons.
 */
@Preview(showBackground = true)
@Composable
fun AllButtonPreviews() {
    val implementations: List<Triple<String, ButtonStyle, Icons>> = listOf(
        Triple("Material3", Material3ButtonStyle(), MaterialIcons)
        // Add future implementations here, e.g., UnstyledDesktop
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        implementations.forEach { (name, style, icons) ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preview: $name")
                CompositionLocalProvider(LocalButtonStyle provides style) {
                    ButtonPreviewsRow { size ->
                        PrimaryButton(
                            onClick = {},
                            size = size,
                            leadingIcon = { icons.add },
                            trailingIcon = { icons.delete }
                        ) { Text("Primary") }
                    }
                    ButtonPreviewsRow { size ->
                        SecondaryButton(
                            onClick = {},
                            size = size,
                            leadingIcon = { icons.add },
                            trailingIcon = { icons.delete }
                        ) { Text("Secondary") }
                    }
                    ButtonPreviewsRow { size ->
                        DangerButton(
                            onClick = {},
                            size = size,
                            leadingIcon = { icons.add },
                            trailingIcon = { icons.delete }
                        ) { Text("Danger") }
                    }
                }
            }
        }
    }
}

/**
 * Helper row to preview all button sizes.
 */
@Composable
private fun ButtonPreviewsRow(content: @Composable (ButtonSize) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        content(ButtonSize.Small)
        content(ButtonSize.Medium)
        content(ButtonSize.Large)
    }
}
