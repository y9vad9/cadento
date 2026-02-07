package cadento.uikit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * A design-system-agnostic Date Picker.
 *
 * @param selectedDate The currently selected date.
 * @param onDateSelected Callback when a date is picked.
 * @param modifier Layout modifier.
 */
@Composable
public fun DatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalDatePickerStyle.current.render(
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        modifier = modifier
    )
}

/**
 * A design-system-agnostic Time Picker.
 *
 * @param selectedTime The currently selected time.
 * @param onTimeSelected Callback when a time is picked.
 * @param modifier Layout modifier.
 */
@Composable
public fun TimePicker(
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalTimePickerStyle.current.render(
        selectedTime = selectedTime,
        onTimeSelected = onTimeSelected,
        modifier = modifier
    )
}

/**
 * Strategy for rendering a [DatePicker].
 */
public interface DatePickerStyle {
    @Composable
    public fun render(
        selectedDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        modifier: Modifier
    )
}

/**
 * Strategy for rendering a [TimePicker].
 */
public interface TimePickerStyle {
    @Composable
    public fun render(
        selectedTime: LocalTime?,
        onTimeSelected: (LocalTime) -> Unit,
        modifier: Modifier
    )
}

public val LocalDatePickerStyle: ProvidableCompositionLocal<DatePickerStyle> = staticCompositionLocalOf {
    error("No DatePickerStyle provided")
}

public val LocalTimePickerStyle: ProvidableCompositionLocal<TimePickerStyle> = staticCompositionLocalOf {
    error("No TimePickerStyle provided")
}
