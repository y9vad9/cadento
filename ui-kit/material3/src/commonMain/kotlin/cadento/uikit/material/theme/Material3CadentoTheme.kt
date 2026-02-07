package cadento.uikit.material.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cadento.uikit.UiColors
import cadento.uikit.material.component.Material3BadgeStyle
import cadento.uikit.material.component.Material3BottomNavBarStyle
import cadento.uikit.material.component.Material3ButtonStyle
import cadento.uikit.material.component.Material3CardStyle
import cadento.uikit.material.component.Material3CheckboxStyle
import cadento.uikit.material.component.Material3DatePickerStyle
import cadento.uikit.material.component.Material3IconButtonStyle
import cadento.uikit.material.component.Material3IconStyle
import cadento.uikit.material.component.Material3NavigationRailStyle
import cadento.uikit.material.component.Material3ProgressStyle
import cadento.uikit.material.component.Material3ScaffoldStyle
import cadento.uikit.material.component.Material3SwitchStyle
import cadento.uikit.material.component.Material3TextInputStyle
import cadento.uikit.material.component.Material3TextStyle
import cadento.uikit.material.component.Material3TimePickerStyle
import cadento.uikit.material.component.Material3TopAppBarStyle
import cadento.uikit.theme.CadentoStyles
import cadento.uikit.theme.CadentoTheme
import androidx.compose.ui.graphics.Color
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

/**
 * Provides a Material 3 implementation of the Cadento theme.
 * 
 * @param useDarkTheme Whether to use the dark color scheme. Defaults to system preference.
 */
@Composable
public fun Material3CadentoTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()
    
    val styles = remember(colorScheme) {
        CadentoStyles(
            buttonStyle = Material3ButtonStyle(),
            textStyle = Material3TextStyle(),
            cardStyle = Material3CardStyle(),
            textInputStyle = Material3TextInputStyle(),
            topAppBarStyle = Material3TopAppBarStyle(),
            scaffoldStyle = Material3ScaffoldStyle(),
            bottomNavBarStyle = Material3BottomNavBarStyle(),
            navigationRailStyle = Material3NavigationRailStyle(),
            iconButtonStyle = Material3IconButtonStyle(),
            checkboxStyle = Material3CheckboxStyle(),
            switchStyle = Material3SwitchStyle(),
            progressStyle = Material3ProgressStyle(),
            badgeStyle = Material3BadgeStyle(),
            iconStyle = Material3IconStyle(),
            datePickerStyle = Material3DatePickerStyle(),
            timePickerStyle = Material3TimePickerStyle(),
            clock = Clock.System,
            timeZone = TimeZone.currentSystemDefault(),
            uiColors = UiColors(
                textPrimary = colorScheme.onBackground,
                textSecondary = colorScheme.onSurfaceVariant,
                surface = colorScheme.surface,
                background = colorScheme.background,
                accent = colorScheme.primary,
                onAccent = colorScheme.onPrimary,
                border = colorScheme.outline,
                success = Color(0xFF4CAF50),
                onSuccess = Color.White,
                warning = Color(0xFFFFC107),
                onWarning = Color.Black,
                error = colorScheme.error,
                onError = colorScheme.onError,
                info = colorScheme.tertiary,
                onInfo = colorScheme.onTertiary
            )
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        androidx.compose.runtime.CompositionLocalProvider(
            cadento.uikit.LocalContentColor provides MaterialTheme.colorScheme.onBackground,
            androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onBackground
        ) {
            CadentoTheme(
                styles = styles,
                content = content
            )
        }
    }
}
