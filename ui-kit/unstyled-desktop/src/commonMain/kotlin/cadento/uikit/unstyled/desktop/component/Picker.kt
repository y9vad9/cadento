package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cadento.uikit.component.DatePickerStyle
import cadento.uikit.component.IconButton
import cadento.uikit.component.Text
import cadento.uikit.component.TextInput
import cadento.uikit.component.TextLevel
import cadento.uikit.component.TimePickerStyle
import cadento.uikit.theme.LocalClock
import cadento.uikit.theme.LocalTimeZone
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

private val PickerCornerRadius = 6.dp
private val DatePickerWidth = 280.dp
private val DatePickerBorderWidth = 1.dp
private val DatePickerPadding = 16.dp
private val DatePickerHeaderSpacing = 16.dp
private val DatePickerGridHeight = 200.dp
private val DayNameBottomPadding = 8.dp
private val GridItemSize = 32.dp
private val DayCornerRadius = 4.dp
private val DayTextPadding = 4.dp

private val TimePickerBorderWidth = 1.dp
private val TimePickerPadding = 12.dp
private val TimePickerArrangementSpacing = 8.dp
private val TimeInputVerticalSpacing = 4.dp
private val TimeIconButtonSize = 24.dp
private val TimeTextInputWidth = 44.dp

/**
 * Unstyled Desktop implementation of [DatePickerStyle].
 */
public class UnstyledDesktopDatePickerStyle(
    private val colors: UnstyledDesktopColors,
    private val defaultDayNames: List<String> = listOf("M", "T", "W", "T", "F", "S", "S")
) : DatePickerStyle {
    @Composable
    override fun render(
        selectedDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        modifier: Modifier
    ) {
        val clock = LocalClock.current
        val timeZone = LocalTimeZone.current
        var viewDate by remember {
            mutableStateOf(selectedDate ?: clock.now().toLocalDateTime(timeZone).date)
        }
        
        val daysInMonth = remember(viewDate) { getMonthLength(viewDate.year, viewDate.month) }
        val dayOfWeekOffset = remember(viewDate) {
            val firstDayOfMonth = LocalDate(viewDate.year, viewDate.month, 1)
            (firstDayOfMonth.dayOfWeek.ordinal) % 7
        }

        Column(
            modifier = modifier
                .width(DatePickerWidth)
                .clip(RoundedCornerShape(PickerCornerRadius))
                .background(colors.surface)
                .border(DatePickerBorderWidth, colors.border, RoundedCornerShape(PickerCornerRadius))
                .padding(DatePickerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewDate = viewDate.minus(1, DateTimeUnit.MONTH) },
                    icon = { Text("<") },
                    contentDescription = "Prev"
                )
                Text("${viewDate.month.name} ${viewDate.year}", level = TextLevel.Label)
                IconButton(
                    onClick = { viewDate = viewDate.plus(1, DateTimeUnit.MONTH) },
                    icon = { Text(">") },
                    contentDescription = "Next"
                )
            }

            Spacer(modifier = Modifier.height(DatePickerHeaderSpacing))

            // Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(DatePickerGridHeight)
            ) {
                // Day names from constructor
                items(defaultDayNames) { name ->
                    Text(
                        name,
                        level = TextLevel.Caption,
                        align = TextAlign.Center,
                        modifier = Modifier.padding(bottom = DayNameBottomPadding)
                    )
                }

                // Empty slots for padding
                items(dayOfWeekOffset) {
                    Box(modifier = Modifier.size(GridItemSize))
                }

                // Actual days
                items(daysInMonth) { dayIndex ->
                    val day = dayIndex + 1
                    val date = LocalDate(viewDate.year, viewDate.month, day)
                    val isSelected = date == selectedDate

                    Box(
                        modifier = Modifier
                            .size(GridItemSize)
                            .clip(RoundedCornerShape(DayCornerRadius))
                            .background(if (isSelected) colors.accent else Color.Transparent)
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            level = TextLevel.Caption,
                            align = TextAlign.Center,
                            modifier = Modifier.padding(DayTextPadding)
                        )
                    }
                }
            }
        }
    }
}

private fun getMonthLength(year: Int, month: Month): Int {
    val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    return when (month) {
        Month.FEBRUARY -> if (isLeapYear) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
}

/**
 * Unstyled Desktop implementation of [TimePickerStyle].
 */
public class UnstyledDesktopTimePickerStyle(private val colors: UnstyledDesktopColors) : TimePickerStyle {
    @Composable
    override fun render(
        selectedTime: LocalTime?,
        onTimeSelected: (LocalTime) -> Unit,
        modifier: Modifier
    ) {
        val time = selectedTime ?: LocalTime(0, 0)

        Row(
            modifier = modifier
                .background(colors.surface, RoundedCornerShape(PickerCornerRadius))
                .border(TimePickerBorderWidth, colors.border, RoundedCornerShape(PickerCornerRadius))
                .padding(TimePickerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TimePickerArrangementSpacing)
        ) {
            AdvancedTimeValueInput(
                value = time.hour,
                max = 23,
                onValueChange = { onTimeSelected(LocalTime(it, time.minute)) }
            )
            
            Text(":", level = TextLevel.Headline)
            
            AdvancedTimeValueInput(
                value = time.minute,
                max = 59,
                onValueChange = { onTimeSelected(LocalTime(time.hour, it)) }
            )
        }
    }
}

@Composable
private fun AdvancedTimeValueInput(
    value: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TimeInputVerticalSpacing)
    ) {
        IconButton(
            onClick = { onValueChange((value + 1) % (max + 1)) },
            icon = { Text("▲") },
            contentDescription = "Increment",
            modifier = Modifier.size(TimeIconButtonSize)
        )

        TextInput(
            value = value.toString().padStart(2, '0'),
            onValueChange = {
                val newValue = it.toIntOrNull()
                if (newValue != null && newValue in 0..max) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.width(TimeTextInputWidth)
        )

        IconButton(
            onClick = { onValueChange(if (value == 0) max else value - 1) },
            icon = { Text("▼") },
            contentDescription = "Decrement",
            modifier = Modifier.size(TimeIconButtonSize)
        )
    }
}