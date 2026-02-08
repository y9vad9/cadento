package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.ButtonSize
import cadento.uikit.component.DangerButton
import cadento.uikit.component.Icon
import cadento.uikit.component.PrimaryButton
import cadento.uikit.component.SecondaryButton
import cadento.uikit.component.Text
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true)
@Composable
fun M3ButtonPreview() {
    Material3Preview {
        ButtonPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopButtonPreview() {
    UnstyledDesktopPreview {
        ButtonPreviewsContent()
    }
}

@Composable
private fun ButtonPreviewsContent() {
    val icons = MaterialIcons
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ButtonPreviewsRow { size ->
            PrimaryButton(
                onClick = {},
                size = size,
                leadingIcon = { Icon(rememberVectorPainter(icons.add), contentDescription = null) },
                trailingIcon = { Icon(rememberVectorPainter(icons.delete), contentDescription = null) }
            ) { Text("Primary") }
        }
        ButtonPreviewsRow { size ->
            SecondaryButton(
                onClick = {},
                size = size,
                leadingIcon = { Icon(rememberVectorPainter(icons.add), contentDescription = null) },
                trailingIcon = { Icon(rememberVectorPainter(icons.delete), contentDescription = null) }
            ) { Text("Secondary") }
        }
        ButtonPreviewsRow { size ->
            DangerButton(
                onClick = {},
                size = size,
                leadingIcon = { Icon(rememberVectorPainter(icons.add), contentDescription = null) },
                trailingIcon = { Icon(rememberVectorPainter(icons.delete), contentDescription = null) }
            ) { Text("Danger") }
        }
    }
}

@Composable
private fun ButtonPreviewsRow(content: @Composable (ButtonSize) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content(ButtonSize.Small)
        content(ButtonSize.Medium)
        content(ButtonSize.Large)
    }
}