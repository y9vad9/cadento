package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cadento.uikit.LocalContentColor
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.component.TopAppBarStyle
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val TopAppBarHeight = 56.dp
private val TopAppBarBorderWidth = 1.dp
private val TopAppBarHorizontalPadding = 16.dp
private val TopAppBarNavIconSpacing = 12.dp

/**
 * Unstyled Desktop implementation of [TopAppBarStyle].
 */
public class UnstyledDesktopTopAppBarStyle(private val colors: UnstyledDesktopColors) : TopAppBarStyle {
    @Composable
    override fun render(
        title: String,
        modifier: Modifier,
        subtitle: String?,
        navigationIcon: (@Composable () -> Unit)?,
        actions: (@Composable () -> Unit)?
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(TopAppBarHeight)
                .background(colors.background)
                .desktopBorder(color = colors.border, bottom = TopAppBarBorderWidth)
                .padding(horizontal = TopAppBarHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationIcon?.let { it() }
            Column(
                modifier = Modifier.weight(1f)
                    .padding(start = if (navigationIcon != null) TopAppBarNavIconSpacing else 0.dp)
            ) {
                Text(text = title, level = TextLevel.Headline)
                subtitle?.let { Text(text = it, level = TextLevel.Caption) }
            }
            actions?.let { it() }
        }
    }
}
