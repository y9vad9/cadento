package cadento.uikit.material.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.ProgressStyle
import cadento.uikit.component.ProgressVariant

public class Material3ProgressStyle : ProgressStyle {
    @Composable
    override fun render(progress: Float?, modifier: Modifier, variant: ProgressVariant) {
        when (variant) {
            ProgressVariant.Circular -> {
                if (progress != null) CircularProgressIndicator(progress = { progress }, modifier = modifier)
                else CircularProgressIndicator(modifier = modifier)
            }
            ProgressVariant.Linear -> {
                if (progress != null) LinearProgressIndicator(progress = { progress }, modifier = modifier)
                else LinearProgressIndicator(modifier = modifier)
            }
        }
    }
}
