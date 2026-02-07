package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import cadento.uikit.component.BottomNavBar
import cadento.uikit.component.Icon
import cadento.uikit.component.NavigationItem
import cadento.uikit.component.NavigationRail
import cadento.uikit.component.Scaffold
import cadento.uikit.component.Text
import cadento.uikit.component.TopAppBar
import cadento.uikit.material.icon.MaterialIcons
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview

@Preview(showBackground = true, widthDp = 1200, heightDp = 1000)
@Composable
fun M3ScaffoldPreview() {
    Material3Preview(scrollable = false) {
        ScaffoldPreviewContent()
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 1000)
@Composable
fun DesktopScaffoldPreview() {
    UnstyledDesktopPreview(scrollable = false) {
        ScaffoldPreviewContent()
    }
}

@Composable
private fun ScaffoldPreviewContent() {
    val navItems = listOf(
        NavigationItem("tasks", "Tasks", { Icon(rememberVectorPainter(MaterialIcons.add), null) }),
        NavigationItem("timers", "Timers", { Icon(rememberVectorPainter(MaterialIcons.delete), null) })
    )

    Scaffold(
        topBar = { TopAppBar(title = "Cadento") },
        bottomBar = {
            BottomNavBar(
                items = navItems,
                selectedItemId = "tasks",
                onItemClick = {}
            )
        },
        startBar = {
            NavigationRail(
                items = navItems,
                selectedItemId = "tasks",
                onItemClick = {},
                header = { Icon(rememberVectorPainter(MaterialIcons.add), null) }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Main Content Area")
            }
        }
    )
}
