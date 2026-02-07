package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import cadento.uikit.component.Icon
import cadento.uikit.component.NavigationItem
import cadento.uikit.component.NavigationRail
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true, heightDp = 600)
@Composable
fun M3NavigationRailPreview() {
    Material3Preview {
        NavigationRailPreviewContent()
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
fun DesktopNavigationRailPreview() {
    UnstyledDesktopPreview {
        NavigationRailPreviewContent()
    }
}

@Composable
private fun NavigationRailPreviewContent() {
    val navItems = listOf(
        NavigationItem("tasks", "Tasks", { Icon(rememberVectorPainter(MaterialIcons.add), null) }),
        NavigationItem("timers", "Timers", { Icon(rememberVectorPainter(MaterialIcons.delete), null) }),
        NavigationItem("settings", "Settings", { Icon(rememberVectorPainter(MaterialIcons.add), null) })
    )

    Box(modifier = Modifier.fillMaxHeight()) {
        NavigationRail(
            items = navItems,
            selectedItemId = "tasks",
            onItemClick = {},
            header = { Icon(rememberVectorPainter(MaterialIcons.add), null) },
            footer = { Icon(rememberVectorPainter(MaterialIcons.delete), null) }
        )
    }
}
