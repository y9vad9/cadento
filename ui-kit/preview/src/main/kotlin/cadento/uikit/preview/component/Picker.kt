package cadento.uikit.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cadento.uikit.component.DatePicker
import cadento.uikit.component.Text
import cadento.uikit.component.TextLevel
import cadento.uikit.component.TimePicker
import cadento.uikit.preview.Material3Preview
import cadento.uikit.preview.UnstyledDesktopPreview
import cadento.uikit.theme.LocalClock
import cadento.uikit.theme.LocalTimeZone
import kotlinx.datetime.toLocalDateTime

@Preview(showBackground = true, name = "M3 Date Picker")
@Composable
fun M3DatePickerPreview() {
    Material3Preview {
        DatePickerPreviewContent()
    }
}

@Preview(showBackground = true, name = "M3 Time Picker")
@Composable
fun M3TimePickerPreview() {
    Material3Preview {
        TimePickerPreviewContent()
    }
}

@Preview(showBackground = true, name = "Desktop Date Picker")
@Composable
fun DesktopDatePickerPreview() {
    UnstyledDesktopPreview {
        DatePickerPreviewContent()
    }
}

@Preview(showBackground = true, name = "Desktop Time Picker")
@Composable
fun DesktopTimePickerPreview() {
    UnstyledDesktopPreview {
        TimePickerPreviewContent()
    }
}

@Composable
private fun DatePickerPreviewContent() {
    val clock = LocalClock.current
    val timeZone = LocalTimeZone.current
    val now = clock.now().toLocalDateTime(timeZone)
    var selectedDate by remember { mutableStateOf(now.date) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Date Picker", level = TextLevel.Label)
        DatePicker(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )
        Text("Selected: $selectedDate", level = TextLevel.Caption)
    }
}

@Composable
private fun TimePickerPreviewContent() {
    val clock = LocalClock.current
    val timeZone = LocalTimeZone.current
    val now = clock.now().toLocalDateTime(timeZone)
    var selectedTime by remember { mutableStateOf(now.time) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Time Picker", level = TextLevel.Label)
        TimePicker(
            selectedTime = selectedTime,
            onTimeSelected = { selectedTime = it }
        )
        Text("Selected: $selectedTime", level = TextLevel.Caption)
    }
}