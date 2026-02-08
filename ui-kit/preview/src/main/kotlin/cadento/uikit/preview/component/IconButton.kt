package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Icon
import cadento.uikit.component.IconButton
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview
import androidx.compose.ui.graphics.Color

@Preview(showBackground = true)
@Composable
fun M3IconButtonPreview() {
    Material3Preview {
        IconButtonPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopIconButtonPreview() {
    UnstyledDesktopPreview {
        IconButtonPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopIconButtonTestColorPreview() {
    UnstyledDesktopPreview {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = {},
                contentDescription = "Add",
                icon = { Icon(rememberVectorPainter(MaterialIcons.add), contentDescription = null, tint = Color.Red) }
            )
            IconButton(
                onClick = {},
                contentDescription = "Delete",
                icon = { Icon(rememberVectorPainter(MaterialIcons.delete), contentDescription = null, tint = Color.Green) },
                enabled = false
            )
        }
    }
}

@Composable
private fun IconButtonPreviewsContent() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(
            onClick = {},
            contentDescription = "Add",
            icon = { Icon(rememberVectorPainter(MaterialIcons.add), contentDescription = null) }
        )
        IconButton(
            onClick = {},
            contentDescription = "Delete",
            icon = { Icon(rememberVectorPainter(MaterialIcons.delete), contentDescription = null) },
            enabled = false
        )
    }
}