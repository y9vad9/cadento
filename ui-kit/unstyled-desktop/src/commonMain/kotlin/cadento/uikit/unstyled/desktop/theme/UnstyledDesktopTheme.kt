package cadento.uikit.unstyled.desktop.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import cadento.uikit.LocalContentColor
import cadento.uikit.UiColors
import cadento.uikit.theme.CadentoStyles
import cadento.uikit.theme.CadentoTheme
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopBadgeStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopBottomNavBarStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopButtonStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopCardStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopCheckboxStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopDatePickerStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopIconButtonStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopIconStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopNavigationRailStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopProgressStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopScaffoldStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopSwitchStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopTextInputStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopTextStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopTimePickerStyle
import cadento.uikit.unstyled.desktop.component.UnstyledDesktopTopAppBarStyle
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

/**
 * Minimalistic desktop color palette.
 */
public data class UnstyledDesktopColors(
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val border: Color,
    val error: Color,
    val onError: Color,
    val onHeader: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color
) {
    public companion object {
        /**
         * Default light color palette.
         */
        public fun light(): UnstyledDesktopColors = UnstyledDesktopColors(
            background = Color.White,
            onBackground = Color(0xFF09090B),
            surface = Color(0xFFF8F8FA),
            onSurface = Color(0xFF09090B),
            primary = Color(0xFF09090B),
            onPrimary = Color.White,
            secondary = Color(0xFF71717A),
            onSecondary = Color.White,
            accent = Color(0xFF2563EB),
            onAccent = Color.White,
            border = Color(0xFFE4E4E7),
            error = Color(0xFFEF4444),
            onError = Color.White,
            onHeader = Color(0xFF71717A),
            success = Color(0xFF22C55E),
            onSuccess = Color.White,
            warning = Color(0xFFF59E0B),
            onWarning = Color.White,
            info = Color(0xFF3B82F6),
            onInfo = Color.White
        )

        /**
         * Default dark color palette.
         */
        public fun dark(): UnstyledDesktopColors = UnstyledDesktopColors(
            background = Color(0xFF09090B),
            onBackground = Color.White,
            surface = Color(0xFF18181B),
            onSurface = Color.White,
            primary = Color.White,
            onPrimary = Color.Black,
            secondary = Color(0xFF3F3F46),
            onSecondary = Color.White,
            accent = Color(0xFF3B82F6),
            onAccent = Color.White,
            border = Color(0xFF27272A),
            error = Color(0xFFF87171),
            onError = Color.White,
            onHeader = Color(0xFFA1A1AA),
            success = Color(0xFF4ADE80),
            onSuccess = Color(0xFF09090B),
            warning = Color(0xFFFBBF24),
            onWarning = Color(0xFF09090B),
            info = Color(0xFF60A5FA),
            onInfo = Color(0xFF09090B)
        )
    }
}

/**
 * Entry point for the Unstyled Desktop design system.
 * 
 * @param colors The color palette to use. Defaults to automatic light/dark based on system.
 * @param useDarkTheme Whether to use dark theme if colors are not provided.
 * @param dayNames The localized day names to use in pickers.
 */
@Composable
public fun UnstyledDesktopTheme(
    colors: UnstyledDesktopColors? = null,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dayNames: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    content: @Composable () -> Unit
) {
    val activeColors = colors ?: if (useDarkTheme) UnstyledDesktopColors.dark() else UnstyledDesktopColors.light()

    val styles = remember(activeColors, dayNames) {
        CadentoStyles(
            buttonStyle = UnstyledDesktopButtonStyle(activeColors),
            textStyle = UnstyledDesktopTextStyle(activeColors),
            cardStyle = UnstyledDesktopCardStyle(activeColors),
            textInputStyle = UnstyledDesktopTextInputStyle(activeColors),
            topAppBarStyle = UnstyledDesktopTopAppBarStyle(activeColors),
            scaffoldStyle = UnstyledDesktopScaffoldStyle(activeColors),
            bottomNavBarStyle = UnstyledDesktopBottomNavBarStyle(activeColors),
            navigationRailStyle = UnstyledDesktopNavigationRailStyle(activeColors),
            iconButtonStyle = UnstyledDesktopIconButtonStyle(activeColors),
            checkboxStyle = UnstyledDesktopCheckboxStyle(activeColors),
            switchStyle = UnstyledDesktopSwitchStyle(activeColors),
            progressStyle = UnstyledDesktopProgressStyle(activeColors),
            badgeStyle = UnstyledDesktopBadgeStyle(activeColors),
            iconStyle = UnstyledDesktopIconStyle(),
            datePickerStyle = UnstyledDesktopDatePickerStyle(activeColors, dayNames),
            timePickerStyle = UnstyledDesktopTimePickerStyle(activeColors),
            clock = Clock.System,
            timeZone = TimeZone.currentSystemDefault(),
            uiColors = UiColors(
                textPrimary = activeColors.onBackground,
                textSecondary = activeColors.onHeader,
                surface = activeColors.surface,
                background = activeColors.background,
                accent = activeColors.accent,
                onAccent = activeColors.onAccent,
                border = activeColors.border,
                success = activeColors.success,
                onSuccess = activeColors.onSuccess,
                warning = activeColors.warning,
                onWarning = activeColors.onWarning,
                error = activeColors.error,
                onError = activeColors.onError,
                info = activeColors.info,
                onInfo = activeColors.onInfo
            )
        )
    }

    CompositionLocalProvider(
        LocalContentColor provides activeColors.onBackground
    ) {
        CadentoTheme(
            styles = styles,
            content = content
        )
    }
}
