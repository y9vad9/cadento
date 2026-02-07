package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.BottomNavBarStyle
import cadento.uikit.component.NavigationItem
import cadento.uikit.component.NavigationRailStyle
import cadento.uikit.component.ScaffoldStyle
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val CompactRailWidthConstant = 52.dp
private val ItemCornerRadius = 8.dp
private val NavRailVerticalPadding = 12.dp
private val NavRailHeaderSpacing = 16.dp
private val NavRailItemSpacing = 8.dp
private val NavItemSize = 44.dp
private val NavIconSize = 24.dp
private val BottomBarHeightStandard = 56.dp
private val BottomBarItemPadding = 8.dp

/**
 * Unstyled Desktop implementation of [ScaffoldStyle].
 */
public class UnstyledDesktopScaffoldStyle(private val colors: UnstyledDesktopColors) : ScaffoldStyle {
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
        Box(modifier = modifier.fillMaxSize().background(colors.background)) {
            SubcomposeLayout { constraints ->
                val layoutWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
                val layoutHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else 0

                val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                // 1. Measure bars in order of priority for space allocation
                // Top bar takes full width
                val topBarPlaceables = subcompose("topBar", topBar).map { it.measure(looseConstraints) }
                val topBarHeight = topBarPlaceables.maxOfOrNull { it.height } ?: 0

                // Side bars take height under top bar
                val sideBarConstraints = looseConstraints.copy(
                    maxHeight = if (constraints.hasBoundedHeight) (layoutHeight - topBarHeight).coerceAtLeast(0) else Constraints.Infinity
                )
                val startBarPlaceables = subcompose("startBar", startBar).map { it.measure(sideBarConstraints) }
                val startBarWidth = startBarPlaceables.maxOfOrNull { it.width } ?: 0

                val endBarPlaceables = subcompose("endBar", endBar).map { it.measure(sideBarConstraints) }
                val endBarWidth = endBarPlaceables.maxOfOrNull { it.width } ?: 0

                // Bottom bar takes width between side bars
                val bottomBarConstraints = looseConstraints.copy(
                    maxWidth = if (constraints.hasBoundedWidth) (layoutWidth - startBarWidth - endBarWidth).coerceAtLeast(0) else Constraints.Infinity
                )
                val bottomBarPlaceables = subcompose("bottomBar", bottomBar).map { it.measure(bottomBarConstraints) }
                val bottomBarHeight = bottomBarPlaceables.maxOfOrNull { it.height } ?: 0

                // 2. Measure content with calculated padding
                val contentConstraints = constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = if (constraints.hasBoundedWidth) {
                        (constraints.maxWidth - startBarWidth - endBarWidth).coerceAtLeast(0)
                    } else {
                        Constraints.Infinity
                    },
                    maxHeight = if (constraints.hasBoundedHeight) {
                        (constraints.maxHeight - topBarHeight - bottomBarHeight).coerceAtLeast(0)
                    } else {
                        Constraints.Infinity
                    }
                )

                val paddingValues = PaddingValues(
                    top = topBarHeight.toDp(),
                    bottom = bottomBarHeight.toDp(),
                    start = startBarWidth.toDp(),
                    end = endBarWidth.toDp()
                )

                val contentPlaceables = subcompose("content") {
                    content(paddingValues)
                }.map { it.measure(contentConstraints) }

                val finalWidth = if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    (contentPlaceables.maxOfOrNull { it.width } ?: 0) + startBarWidth + endBarWidth
                }

                val finalHeight = if (constraints.hasBoundedHeight) {
                    constraints.maxHeight
                } else {
                    (contentPlaceables.maxOfOrNull { it.height } ?: 0) + topBarHeight + bottomBarHeight
                }

                layout(finalWidth, finalHeight) {
                    // Place content
                    contentPlaceables.forEach { it.place(startBarWidth, topBarHeight) }

                    // Place bars
                    topBarPlaceables.forEach { it.place(0, 0) }
                    // Bottom bar is placed between startBar and endBar
                    bottomBarPlaceables.forEach { it.place(startBarWidth, finalHeight - bottomBarHeight) }
                    // Side bars are full height under top bar (they go down to the bottom of the screen)
                    startBarPlaceables.forEach { it.place(0, topBarHeight) }
                    endBarPlaceables.forEach { it.place(finalWidth - endBarWidth, topBarHeight) }
                }
            }

            // Drawers and Snackbars on top
            Box(Modifier.fillMaxSize()) { snackbarHost() }
            Box(Modifier.fillMaxSize()) { drawer() }
        }
    }
}

/**
 * Unstyled Desktop implementation of [NavigationRailStyle].
 */
public class UnstyledDesktopNavigationRailStyle(private val colors: UnstyledDesktopColors) : NavigationRailStyle {
    @Composable
    override fun render(
        items: List<NavigationItem>,
        selectedItemId: String?,
        onItemClick: (NavigationItem) -> Unit,
        modifier: Modifier,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?
    ) {
        Column(
            modifier = modifier
                .width(CompactRailWidthConstant)
                .fillMaxHeight()
                .desktopBorder(color = colors.border, end = 1.dp)
                .padding(vertical = NavRailVerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            header?.let { it() }
            Spacer(modifier = Modifier.height(NavRailHeaderSpacing))
            
            items.forEach { item ->
                NavigationRailItem(
                    item = item,
                    isSelected = item.id == selectedItemId,
                    onClick = { onItemClick(item) },
                    colors = colors
                )
                Spacer(modifier = Modifier.height(NavRailItemSpacing))
            }

            Spacer(modifier = Modifier.weight(1f))
            footer?.let { it() }
        }
    }
}

@Composable
private fun NavigationRailItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: UnstyledDesktopColors
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isSelected -> colors.onBackground
        isHovered -> colors.border.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(NavItemSize)
            .clip(RoundedCornerShape(ItemCornerRadius))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentColor = if (isSelected) colors.background else colors.onBackground
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(modifier = Modifier.size(NavIconSize)) {
                if (isSelected) {
                    item.selectedIcon?.let { it() } ?: item.icon()
                } else {
                    item.icon()
                }
            }
        }
    }
}

/**
 * Unstyled Desktop implementation of [BottomNavBarStyle].
 */
public class UnstyledDesktopBottomNavBarStyle(private val colors: UnstyledDesktopColors) : BottomNavBarStyle {
    @Composable
    override fun render(
        items: List<NavigationItem>, 
        selectedItemId: String?, 
        onItemClick: (NavigationItem) -> Unit, 
        modifier: Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(BottomBarHeightStandard)
                .background(colors.background)
                .desktopBorder(color = colors.border, top = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = item.id == selectedItemId
                
                Column(
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(BottomBarItemPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(NavIconSize)) {
                        if (isSelected) {
                            item.selectedIcon?.let { it() } ?: item.icon()
                        } else {
                            item.icon()
                        }
                    }
                    Text(item.label, level = TextLevel.Caption)
                }
            }
        }
    }
}
