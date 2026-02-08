package cadento.uikit.material.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar as M3TopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.TopAppBarStyle

public class Material3TopAppBarStyle : TopAppBarStyle {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun render(
        title: String,
        modifier: Modifier,
        subtitle: String?,
        navigationIcon: (@Composable () -> Unit)?,
        actions: (@Composable () -> Unit)?
    ) {
        val titleContent: @Composable () -> Unit = {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (subtitle != null) {
            MediumTopAppBar(
                title = titleContent,
                modifier = modifier,
                navigationIcon = navigationIcon ?: {},
                actions = { actions?.let { it() } }
            )
        } else {
            M3TopAppBar(
                title = { Text(title) },
                modifier = modifier,
                navigationIcon = navigationIcon ?: {},
                actions = { actions?.let { it() } }
            )
        }
    }
}
