package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Icon
import cadento.uikit.component.TextInput
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true, widthDp = 600)
@Composable
fun M3TextInputPreview() {
    Material3Preview {
        TextInputPreviewsContent()
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun DesktopTextInputPreview() {
    UnstyledDesktopPreview {
        TextInputPreviewsContent()
    }
}

@Composable
private fun TextInputPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextInput(
            value = "Normal text",
            onValueChange = {},
            label = "Single Line Label",
            placeholder = "Placeholder",
            supportingText = "Supporting text"
        )
        
        TextInput(
            value = "Multiline text that grows as you type more content into the field...",
            onValueChange = {},
            label = "Multiline Label",
            isMultiline = true,
            maxLines = 3
        )

        TextInput(
            value = "Error value",
            onValueChange = {},
            label = "Error Label",
            error = "This field is required",
            leadingIcon = { Icon(rememberVectorPainter(MaterialIcons.add), null) }
        )

        TextInput(
            value = "Read only text",
            onValueChange = {},
            label = "Read Only",
            isReadOnly = true,
            trailingIcon = { Icon(rememberVectorPainter(MaterialIcons.delete), null) }
        )
    }
}
