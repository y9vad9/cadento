package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Card
import cadento.uikit.component.CardVariant
import cadento.uikit.component.Text
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true)
@Composable
fun M3CardPreview() {
    Material3Preview {
        CardPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopCardPreview() {
    UnstyledDesktopPreview {
        CardPreviewsContent()
    }
}

@Composable
private fun CardPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CardVariant.entries.forEach { variant ->
            Card(
                variant = variant,
                header = { Text("Header (${variant.name})", modifier = Modifier.padding(8.dp)) },
                footer = { Text("Footer", modifier = Modifier.padding(8.dp)) }
            ) {
                Text("This is a ${variant.name} emphasis card content.", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
