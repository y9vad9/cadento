package cadento.uikit.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cadento.uikit.material.theme.Material3CadentoTheme
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopTheme

/**
 * Wrapper for Material 3 specific previews.
 */
@Composable
fun Material3Preview(
    scrollable: Boolean = true,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val modifier = if (scrollable) Modifier.verticalScroll(scrollState) else Modifier
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Material 3 - Light")
            Material3CadentoTheme(useDarkTheme = false) {
                Box(modifier = Modifier.background(Color.White).padding(8.dp)) {
                    content()
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Material 3 - Dark")
            Material3CadentoTheme(useDarkTheme = true) {
                Box(modifier = Modifier.background(Color(0xFF1C1B1F)).padding(8.dp)) {
                    content()
                }
            }
        }
    }
}

/**
 * Wrapper for Unstyled Desktop specific previews.
 */
@Composable
fun UnstyledDesktopPreview(
    dayNames: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    scrollable: Boolean = true,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val modifier = if (scrollable) Modifier.verticalScroll(scrollState) else Modifier

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Unstyled Desktop - Light")
            UnstyledDesktopTheme(useDarkTheme = false, dayNames = dayNames) {
                Box(modifier = Modifier.background(Color.White).padding(8.dp)) {
                    content()
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Unstyled Desktop - Dark")
            UnstyledDesktopTheme(useDarkTheme = true, dayNames = dayNames) {
                Box(modifier = Modifier.background(Color(0xFF09090B)).padding(8.dp)) {
                    content()
                }
            }
        }
    }
}