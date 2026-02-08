package cadento.uikit.material.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import cadento.uikit.component.DatePickerStyle
import cadento.uikit.component.TimePickerStyle
import cadento.uikit.theme.LocalTimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Instant

/**
 * Material 3 implementation of [DatePickerStyle].
 *
 * NOTE: The underlying Material 3 DatePicker implementation does not support custom day names.
 */
public class Material3DatePickerStyle : DatePickerStyle {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun render(
        selectedDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        modifier: Modifier
    ) {
        val timeZone = LocalTimeZone.current
        
        // Key the state by both timeZone and selectedDate to ensure re-initialization on external changes
        val state = key(timeZone, selectedDate) {
            rememberDatePickerState(
                initialSelectedDateMillis = selectedDate?.atStartOfDayIn(timeZone)?.toEpochMilliseconds()
            )
        }

        // Sync state back to caller
        val currentSelection = state.selectedDateMillis
        LaunchedEffect(currentSelection, timeZone) {
            if (currentSelection != null) {
                val date = Instant.fromEpochMilliseconds(currentSelection)
                    .toLocalDateTime(timeZone)
                    .date
                if (date != selectedDate) {
                    onDateSelected(date)
                }
            }
        }

        DatePicker(
            state = state,
            modifier = modifier
        )
    }
}

/**
 * Material 3 implementation of [TimePickerStyle].
 */
public class Material3TimePickerStyle : TimePickerStyle {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun render(
        selectedTime: LocalTime?,
        onTimeSelected: (LocalTime) -> Unit,
        modifier: Modifier
    ) {
        val timeZone = LocalTimeZone.current
        
        // Key the state by both timeZone and selectedTime
        val state = key(timeZone, selectedTime) {
            rememberTimePickerState(
                initialHour = selectedTime?.hour ?: 0,
                initialMinute = selectedTime?.minute ?: 0,
                is24Hour = true
            )
        }

        // Sync state back to caller
        LaunchedEffect(state.hour, state.minute, timeZone) {
            val newTime = LocalTime(state.hour, state.minute)
            if (newTime != selectedTime) {
                onTimeSelected(newTime)
            }
        }

        TimePicker(
            state = state,
            modifier = modifier
        )
    }
}
