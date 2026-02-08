package cadento.uikit.material.component

import androidx.compose.material3.Checkbox as M3Checkbox
import androidx.compose.material3.Switch as M3Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.CheckboxStyle
import cadento.uikit.component.SwitchStyle

public class Material3CheckboxStyle : CheckboxStyle {
    @Composable
    override fun render(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, enabled: Boolean) {
        M3Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled
        )
    }
}

public class Material3SwitchStyle : SwitchStyle {
    @Composable
    override fun render(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, enabled: Boolean) {
        M3Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled
        )
    }
}
