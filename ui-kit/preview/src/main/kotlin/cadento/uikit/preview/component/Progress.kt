package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.ProgressIndicator
import cadento.uikit.component.ProgressVariant
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true, widthDp = 600)
@Composable
fun M3ProgressPreview() {
    Material3Preview {
        ProgressPreviewsContent()
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun DesktopProgressPreview() {
    UnstyledDesktopPreview {
        ProgressPreviewsContent()
    }
}

@Composable
private fun ProgressPreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Circular - Determinate (75%)", level = TextLevel.Label)
        ProgressIndicator(progress = 0.75f, variant = ProgressVariant.Circular)

        Text("Circular - Indeterminate", level = TextLevel.Label)
        ProgressIndicator(variant = ProgressVariant.Circular)

        Text("Linear - Determinate (40%)", level = TextLevel.Label)
        ProgressIndicator(
            progress = 0.4f, 
            variant = ProgressVariant.Linear,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Linear - Indeterminate", level = TextLevel.Label)
        ProgressIndicator(
            variant = ProgressVariant.Linear,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
