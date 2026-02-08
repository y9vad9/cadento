package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Checkbox
import cadento.uikit.component.Switch
import cadento.uikit.component.Text
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview
import kotlin.time.Clock

@Preview(showBackground = true)
@Composable
fun M3SelectionPreview() {
    Material3Preview {
        SelectionPreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopSelectionPreview() {
    UnstyledDesktopPreview {
        SelectionPreviewsContent()
    }
}

@Composable
private fun SelectionPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = true, onCheckedChange = {})
                Text("Checked Checkbox", modifier = Modifier.padding(start = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("Unchecked Checkbox", modifier = Modifier.padding(start = 8.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = true, onCheckedChange = {})
                Text("Switch ON", modifier = Modifier.padding(start = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = false, onCheckedChange = {})
                Text("Switch OFF", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
