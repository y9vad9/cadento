package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware checkbox component.
 */
@Composable
public fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style = LocalCheckboxStyle.current
    style.render(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled
    )
}

/**
 * A theme-aware switch/toggle component.
 */
@Composable
public fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style = LocalSwitchStyle.current
    style.render(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled
    )
}

public interface CheckboxStyle {
    @Composable
    public fun render(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier,
        enabled: Boolean
    )
}

public interface SwitchStyle {
    @Composable
    public fun render(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier,
        enabled: Boolean
    )
}

public val LocalCheckboxStyle: ProvidableCompositionLocal<CheckboxStyle> = staticCompositionLocalOf {
    error("No CheckboxStyle provided")
}

public val LocalSwitchStyle: ProvidableCompositionLocal<SwitchStyle> = staticCompositionLocalOf {
    error("No SwitchStyle provided")
}
