package cadento.uikit.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware scaffold component with flexible navigation slots.
 *
 * @param modifier Modifier to apply
 * @param topBar Optional top bar slot
 * @param bottomBar Optional bottom bar slot (usually BottomNavBar)
 * @param startBar Optional start (left) bar slot (usually NavigationRail or permanent Drawer)
 * @param endBar Optional end (right) bar slot
 * @param drawer Optional modal drawer slot
 * @param snackbarHost Optional snackbar host slot
 * @param content Main content slot
 */
@Composable
public fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    startBar: @Composable () -> Unit = {},
    endBar: @Composable () -> Unit = {},
    drawer: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val style = LocalScaffoldStyle.current
    style.render(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        startBar = startBar,
        endBar = endBar,
        drawer = drawer,
        snackbarHost = snackbarHost,
        content = content
    )
}

/**
 * Navigation item metadata used by navigation bars and rails.
 *
 * @param id Unique identifier for the item.
 * @param label Human-readable name.
 * @param icon Primary icon.
 * @param selectedIcon Optional icon to be used when the item is active.
 */
public data class NavigationItem(
    val id: String,
    val label: String,
    val icon: @Composable () -> Unit,
    val selectedIcon: (@Composable () -> Unit)? = null,
)

/**
 * A theme-aware bottom navigation bar.
 *
 * @param items List of navigation options.
 * @param selectedItemId ID of the currently active item.
 * @param onItemClick Callback for user selection.
 * @param modifier Modifier for the bar.
 */
@Composable
public fun BottomNavBar(
    items: List<NavigationItem>,
    selectedItemId: String?,
    onItemClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = LocalBottomNavBarStyle.current
    style.render(
        items = items,
        selectedItemId = selectedItemId,
        onItemClick = onItemClick,
        modifier = modifier
    )
}

/**
 * A theme-aware navigation rail.
 *
 * @param items List of primary navigation items.
 * @param selectedItemId ID of the active item.
 * @param onItemClick Callback for selection.
 * @param modifier Modifier for the rail.
 * @param header Content at the top of the rail.
 * @param footer Content at the bottom of the rail.
 */
@Composable
public fun NavigationRail(
    items: List<NavigationItem>,
    selectedItemId: String?,
    onItemClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val style = LocalNavigationRailStyle.current
    style.render(
        items = items,
        selectedItemId = selectedItemId,
        onItemClick = onItemClick,
        modifier = modifier,
        header = header,
        footer = footer
    )
}

// Interfaces

/**
 * Strategy for rendering the main layout shell.
 */
public interface ScaffoldStyle {
    /**
     * Renders a flexible layout with multiple navigation slots.
     *
     * @param modifier Modifier for the entire scaffold.
     * @param topBar Top navigation bar slot.
     * @param bottomBar Bottom navigation bar slot.
     * @param startBar Side navigation slot (left in LTR).
     * @param endBar Secondary side panel slot (right in LTR).
     * @param drawer Modal navigation drawer slot.
     * @param snackbarHost Container for temporary feedback messages.
     * @param content Main scrollable or interactive content area.
     */
    @Composable
    public fun render(
        modifier: Modifier,
        topBar: @Composable () -> Unit,
        bottomBar: @Composable () -> Unit,
        startBar: @Composable () -> Unit,
        endBar: @Composable () -> Unit,
        drawer: @Composable () -> Unit,
        snackbarHost: @Composable () -> Unit,
        content: @Composable (PaddingValues) -> Unit
    )
}

/**
 * Strategy for rendering a [BottomNavBar].
 */
public interface BottomNavBarStyle {
    /**
     * @param items List of navigation options.
     * @param selectedItemId ID of the currently active item.
     * @param onItemClick Callback for user selection.
     * @param modifier Modifier for the bar.
     */
    @Composable
    public fun render(
        items: List<NavigationItem>,
        selectedItemId: String?,
        onItemClick: (NavigationItem) -> Unit,
        modifier: Modifier
    )
}

/**
 * Strategy for rendering a [NavigationRail].
 */
public interface NavigationRailStyle {
    /**
     * @param items List of primary navigation items.
     * @param selectedItemId ID of the active item.
     * @param onItemClick Callback for selection.
     * @param modifier Modifier for the rail.
     * @param header Content at the top of the rail.
     * @param footer Content at the bottom of the rail.
     */
    @Composable
    public fun render(
        items: List<NavigationItem>,
        selectedItemId: String?,
        onItemClick: (NavigationItem) -> Unit,
        modifier: Modifier,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?
    )
}

// CompositionLocals

public val LocalScaffoldStyle: ProvidableCompositionLocal<ScaffoldStyle> = staticCompositionLocalOf {
    error("No ScaffoldStyle provided")
}

public val LocalBottomNavBarStyle: ProvidableCompositionLocal<BottomNavBarStyle> = staticCompositionLocalOf {
    error("No BottomNavBarStyle provided")
}

public val LocalNavigationRailStyle: ProvidableCompositionLocal<NavigationRailStyle> = staticCompositionLocalOf {
    error("No NavigationRailStyle provided")
}