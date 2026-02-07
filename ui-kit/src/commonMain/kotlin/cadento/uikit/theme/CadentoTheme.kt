package cadento.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cadento.uikit.LocalUiColors
import cadento.uikit.UiColors
import cadento.uikit.component.BadgeStyle
import cadento.uikit.component.BottomNavBarStyle
import cadento.uikit.component.ButtonStyle
import cadento.uikit.component.CardStyle
import cadento.uikit.component.CheckboxStyle
import cadento.uikit.component.DatePickerStyle
import cadento.uikit.component.IconButtonStyle
import cadento.uikit.component.IconStyle
import cadento.uikit.component.LocalBadgeStyle
import cadento.uikit.component.LocalBottomNavBarStyle
import cadento.uikit.component.LocalButtonStyle
import cadento.uikit.component.LocalCardStyle
import cadento.uikit.component.LocalCheckboxStyle
import cadento.uikit.component.LocalDatePickerStyle
import cadento.uikit.component.LocalIconButtonStyle
import cadento.uikit.component.LocalIconStyle
import cadento.uikit.component.LocalNavigationRailStyle
import cadento.uikit.component.LocalProgressStyle
import cadento.uikit.component.LocalScaffoldStyle
import cadento.uikit.component.LocalSwitchStyle
import cadento.uikit.component.LocalTextInputStyle
import cadento.uikit.component.LocalTextStyle
import cadento.uikit.component.LocalTimePickerStyle
import cadento.uikit.component.LocalTopAppBarStyle
import cadento.uikit.component.NavigationRailStyle
import cadento.uikit.component.ProgressStyle
import cadento.uikit.component.ScaffoldStyle
import cadento.uikit.component.SwitchStyle
import cadento.uikit.component.TextInputStyle
import cadento.uikit.component.TextStyle
import cadento.uikit.component.TimePickerStyle
import cadento.uikit.component.TopAppBarStyle
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

/**
 * Data class containing all the style providers for the theme.
 */
public data class CadentoStyles(
    val buttonStyle: ButtonStyle,
    val textStyle: TextStyle,
    val cardStyle: CardStyle,
    val textInputStyle: TextInputStyle,
    val topAppBarStyle: TopAppBarStyle,
    val scaffoldStyle: ScaffoldStyle,
    val bottomNavBarStyle: BottomNavBarStyle,
    val navigationRailStyle: NavigationRailStyle,
    val iconButtonStyle: IconButtonStyle,
    val checkboxStyle: CheckboxStyle,
    val switchStyle: SwitchStyle,
    val progressStyle: ProgressStyle,
    val badgeStyle: BadgeStyle,
    val iconStyle: IconStyle,
    val datePickerStyle: DatePickerStyle,
    val timePickerStyle: TimePickerStyle,
    val clock: Clock,
    val timeZone: TimeZone,
    val uiColors: UiColors
)

/**
 * Global theme entry point for Cadento.
 */
@Composable
public fun CadentoTheme(
    styles: CadentoStyles,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalButtonStyle provides styles.buttonStyle,
        LocalTextStyle provides styles.textStyle,
        LocalCardStyle provides styles.cardStyle,
        LocalTextInputStyle provides styles.textInputStyle,
        LocalTopAppBarStyle provides styles.topAppBarStyle,
        LocalScaffoldStyle provides styles.scaffoldStyle,
        LocalBottomNavBarStyle provides styles.bottomNavBarStyle,
        LocalNavigationRailStyle provides styles.navigationRailStyle,
        LocalIconButtonStyle provides styles.iconButtonStyle,
        LocalCheckboxStyle provides styles.checkboxStyle,
        LocalSwitchStyle provides styles.switchStyle,
        LocalProgressStyle provides styles.progressStyle,
        LocalBadgeStyle provides styles.badgeStyle,
        LocalIconStyle provides styles.iconStyle,
        LocalDatePickerStyle provides styles.datePickerStyle,
        LocalTimePickerStyle provides styles.timePickerStyle,
        LocalClock provides styles.clock,
        LocalTimeZone provides styles.timeZone,
        LocalUiColors provides styles.uiColors,
        content = content
    )
}