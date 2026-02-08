package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true)
@Composable
fun M3TextPreview() {
    Material3Preview {
        TextPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopTextPreview() {
    UnstyledDesktopPreview {
        TextPreviewsContent()
    }
}

@Composable
private fun TextPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextLevel.entries.forEach { level ->
            Text(text = "Text Level: ${level.name}", level = level)
        }
    }
}
