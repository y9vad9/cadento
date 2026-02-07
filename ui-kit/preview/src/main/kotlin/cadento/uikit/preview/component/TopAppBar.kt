package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Icon
import cadento.uikit.component.IconButton
import cadento.uikit.component.TopAppBar
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true)
@Composable
fun M3TopAppBarPreview() {
    Material3Preview {
        TopAppBarPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopTopAppBarPreview() {
    UnstyledDesktopPreview {
        TopAppBarPreviewsContent()
    }
}

@Composable
private fun TopAppBarPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TopAppBar(
            title = "Simple Title"
        )
        
        TopAppBar(
            title = "Main Title",
            subtitle = "Optional Subtitle",
            navigationIcon = { 
                IconButton(
                    onClick = {},
                    contentDescription = null,
                    icon = { Icon(rememberVectorPainter(MaterialIcons.add), contentDescription = null) }
                )
            }
        )

        TopAppBar(
            title = "With Actions",
            actions = {
                IconButton(
                    onClick = {},
                    contentDescription = null,
                    icon = { Icon(rememberVectorPainter(MaterialIcons.add), contentDescription = null) }
                )
                IconButton(
                    onClick = {},
                    contentDescription = null,
                    icon = { Icon(rememberVectorPainter(MaterialIcons.delete), contentDescription = null) }
                )
            }
        )
    }
}