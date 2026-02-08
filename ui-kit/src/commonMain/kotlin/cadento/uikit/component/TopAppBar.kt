package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware top app bar component.
 *
 * @param title Primary title
 * @param modifier Modifier to apply
 * @param subtitle Optional subtitle
 * @param navigationIcon Optional navigation icon slot (e.g. Menu, Back)
 * @param actions Optional actions slot
 */
@Composable
public fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    val style = LocalTopAppBarStyle.current
    style.render(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        navigationIcon = navigationIcon,
        actions = actions
    )
}

/**
 * Strategy for rendering a top app bar.
 */
public interface TopAppBarStyle {
    /**
     * @param title Main title text.
     * @param modifier Modifier for the container.
     * @param subtitle Optional secondary text.
     * @param navigationIcon Leading icon (e.g. Back, Menu).
     * @param actions Trailing icons/actions.
     */
    @Composable
    public fun render(
        title: String,
        modifier: Modifier,
        subtitle: String?,
        navigationIcon: (@Composable () -> Unit)?,
        actions: (@Composable () -> Unit)?
    )
}

public val LocalTopAppBarStyle: ProvidableCompositionLocal<TopAppBarStyle> = staticCompositionLocalOf {
    error("No TopAppBarStyle provided")
}
