package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * A theme-aware progress indicator.
 *
 * @param progress Current progress [0.0, 1.0]. If null, renders an indeterminate state.
 * @param modifier Layout modifier.
 * @param variant Visual variant (Circular or Linear).
 */
@Composable
public fun ProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Circular,
) {
    val style = LocalProgressStyle.current
    style.render(
        progress = progress,
        modifier = modifier,
        variant = variant
    )
}

public enum class ProgressVariant {
    Circular,
    Linear
}

public interface ProgressStyle {
    @Composable
    public fun render(
        progress: Float?,
        modifier: Modifier,
        variant: ProgressVariant
    )
}

public val LocalProgressStyle: ProvidableCompositionLocal<ProgressStyle> = staticCompositionLocalOf {
    error("No ProgressStyle provided")
}
