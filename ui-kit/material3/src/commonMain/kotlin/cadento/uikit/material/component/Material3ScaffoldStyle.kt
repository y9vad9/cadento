package cadento.uikit.material.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.BottomNavBarStyle
import cadento.uikit.component.NavigationItem
import cadento.uikit.component.NavigationRailStyle
import cadento.uikit.component.ScaffoldStyle
import androidx.compose.material3.NavigationRail as M3NavigationRail
import androidx.compose.material3.Scaffold as M3Scaffold

public class Material3ScaffoldStyle : ScaffoldStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        topBar: @Composable () -> Unit,
        bottomBar: @Composable () -> Unit,
        startBar: @Composable () -> Unit,
        endBar: @Composable () -> Unit,
        drawer: @Composable () -> Unit,
        snackbarHost: @Composable () -> Unit,
        content: @Composable (PaddingValues) -> Unit
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerContent = drawer,
            drawerState = drawerState,
            gesturesEnabled = true
        ) {
            // Material 3 doesn't natively support multiple rails/sidebars in Scaffold.
            // We wrap the Scaffold content in a Row to support startBar and endBar.
            M3Scaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                content = { padding ->
                    Row(modifier = Modifier.fillMaxSize().padding(padding)) {
                        startBar()
                        Box(modifier = Modifier.weight(1f)) {
                            content(PaddingValues())
                        }
                        endBar()
                    }
                }
            )
        }
    }
}

public class Material3BottomNavBarStyle : BottomNavBarStyle {
    @Composable
    override fun render(
        items: List<NavigationItem>,
        selectedItemId: String?,
        onItemClick: (NavigationItem) -> Unit,
        modifier: Modifier
    ) {
        NavigationBar(modifier = modifier) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = item.id == selectedItemId,
                    onClick = { onItemClick(item) },
                    icon = item.icon,
                    label = { Text(item.label) }
                )
            }
        }
    }
}

public class Material3NavigationRailStyle : NavigationRailStyle {
    @Composable
    override fun render(
        items: List<NavigationItem>,
        selectedItemId: String?,
        onItemClick: (NavigationItem) -> Unit,
        modifier: Modifier,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?
    ) {
        val m3Header: @Composable (ColumnScope.() -> Unit)? = header?.let {
            { it() }
        }

        M3NavigationRail(
            modifier = modifier.fillMaxHeight(),
            header = m3Header,
        ) {
            items.forEach { item ->
                NavigationRailItem(
                    selected = item.id == selectedItemId,
                    onClick = { onItemClick(item) },
                    icon = item.icon,
                    label = { Text(item.label) }
                )
            }

            if (footer != null) {
                Spacer(modifier = Modifier.weight(1f))
                footer()
            }
        }
    }
}