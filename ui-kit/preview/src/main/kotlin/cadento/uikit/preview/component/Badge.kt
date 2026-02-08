package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.Badge
import cadento.uikit.component.BadgeIntent
import cadento.uikit.component.BadgeVariant
import cadento.uikit.component.Icon
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true)
@Composable
fun M3BadgePreview() {
    Material3Preview {
        BadgePreviewsContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DesktopBadgePreview() {
    UnstyledDesktopPreview {
        BadgePreviewsContent()
    }
}

@Composable
private fun BadgePreviewsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Anchored Badges (Intents)", level = TextLevel.Label)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Badge(
                variant = BadgeVariant.Dot,
                intent = BadgeIntent.Primary,
                container = { Icon(rememberVectorPainter(MaterialIcons.add), null, modifier = Modifier.size(24.dp)) }
            )
            
            Badge(
                variant = BadgeVariant.Count,
                intent = BadgeIntent.Error,
                content = { Text("5", level = TextLevel.Caption) },
                container = { Icon(rememberVectorPainter(MaterialIcons.delete), null, modifier = Modifier.size(24.dp)) }
            )
        }
        Text("Standalone Colored Labels", level = TextLevel.Label)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Badge(variant = BadgeVariant.Label, intent = BadgeIntent.Primary, content = { Text("primary") })
            Badge(variant = BadgeVariant.Label, intent = BadgeIntent.Success, content = { Text("success") })
            Badge(variant = BadgeVariant.Label, intent = BadgeIntent.Warning, content = { Text("warning") })
            Badge(variant = BadgeVariant.Label, intent = BadgeIntent.Error, content = { Text("error") })
        }

        Text("Outlined Colored Variants", level = TextLevel.Label)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Badge(variant = BadgeVariant.Outlined, intent = BadgeIntent.Primary, content = { Text("Verified") })
            Badge(variant = BadgeVariant.Outlined, intent = BadgeIntent.Success, content = { Text("Completed") })
            Badge(variant = BadgeVariant.Outlined, intent = BadgeIntent.Info, content = { Text("Draft") })
        }
    }
}
