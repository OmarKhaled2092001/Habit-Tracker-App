package com.example.habittrackerapp.ui.screens.habit_information.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star // Another example for Overdone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.habit_information.CalendarDayStatus // Assuming CalendarDayStatus is in this package
import com.example.habittrackerapp.ui.screens.habit_information.CalendarDayUIData // Assuming CalendarDayUIData is in this package
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarSection(
    currentMonth: YearMonth,
    markedDays: Map<Int, CalendarDayUIData>, // Changed to use CalendarDayUIData
    habitColors: HabitInformationColors, // Renamed for clarity, was 'colors'
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (Int) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    // Adjust startOffset to ensure Monday is the first day of the week visually
    val startOffset = (firstDayOfMonth.value - DayOfWeek.MONDAY.value + 7) % 7
    val days = (1..daysInMonth).toList()
    val totalCells = ((startOffset + daysInMonth + 6) / 7) * 7 // Ensure full weeks are displayed
    val calendarCells = List(startOffset) { null } + days + List(totalCells - daysInMonth - startOffset) { null }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month", tint = Color.Gray)
            }
            Text(
                text = currentMonth.format(monthFormatter),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black // Consider using a color from a theme
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month", tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Ensure days of week start from Monday
            val daysOfWeek = DayOfWeek.entries.toMutableList()
            val firstDay = DayOfWeek.MONDAY
            while (daysOfWeek.first() != firstDay) {
                daysOfWeek.add(daysOfWeek.removeAt(0))
            }
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                    fontSize = 12.sp,
                    color = Color.Gray, // Consider using a color from a theme
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            calendarCells.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { day ->
                        val dayUiData = day?.let { markedDays[it] }
                        DateCell(
                            day = day,
                            dayUiData = dayUiData,
                            habitColors = habitColors,
                            onDateSelected = onDateSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateCell(
    day: Int?,
    dayUiData: CalendarDayUIData?,
    habitColors: HabitInformationColors,
    onDateSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp) // Increased size slightly for better touch target and visual balance
            .clip(CircleShape)
            .clickable(enabled = day != null) { day?.let { onDateSelected(it) } },
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            val defaultTextColor = Color.Black
            var textColor = defaultTextColor
            var backgroundModifier = Modifier.background(Color.Transparent) // Default transparent background
            var borderModifier = Modifier.border(0.dp, Color.Transparent, CircleShape) // Default no border
            var overdoneIndicator: @Composable (() -> Unit)? = null

            dayUiData?.let {
                textColor = when (it.status) {
                    CalendarDayStatus.GOAL_MET, CalendarDayStatus.OVERDONE -> habitColors.onPrimary
                    else -> defaultTextColor // Or a specific color for missed/partially met text
                }
                backgroundModifier = Modifier.background(it.displayColor, CircleShape)

                if (it.showOverdoneIndicator) {
                    overdoneIndicator = {
                        Icon(
                            Icons.Filled.Star, // Or CheckCircle, or a custom drawable
                            contentDescription = "Overdone",
                            tint = habitColors.onPrimary.copy(alpha = 0.8f), // Make it slightly subtle or contrasting
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
                if (it.showFutureScheduledIndicator) {
                    borderModifier = Modifier.border(1.5.dp, it.displayColor, CircleShape)
                    // If future scheduled, background might be transparent or a very light color
                    // and text color might be the habit color itself or a muted color.
                    // For now, using displayColor for border as per spec.
                    backgroundModifier = Modifier.background(Color.Transparent) // Ensure no fill for just border
                    textColor = it.displayColor // Text color could be the habit color for future scheduled
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp) // Inner circle for background color
                    .clip(CircleShape)
                    .then(backgroundModifier)
                    .then(borderModifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                overdoneIndicator?.invoke()
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CalendarSectionPreview() {
    val habitBaseColor = Color(0xFF4CAF50) // Green for preview
    val habitColors = generateHabitInformationColors(habitBaseColor)
    val sampleMarkedDays = mapOf(
        1 to CalendarDayUIData(CalendarDayStatus.GOAL_MET, habitColors.primary), // Goal Met
        2 to CalendarDayUIData(CalendarDayStatus.PARTIALLY_MET, habitColors.primaryLight), // Partially Met
        3 to CalendarDayUIData(CalendarDayStatus.OVERDONE, habitColors.primary, showOverdoneIndicator = true), // Overdone
        4 to CalendarDayUIData(CalendarDayStatus.MISSED, Color.LightGray.copy(alpha = 0.6f)), // Missed
        5 to CalendarDayUIData(CalendarDayStatus.NOT_SCHEDULED, Color.Transparent), // Not Scheduled
        10 to CalendarDayUIData(CalendarDayStatus.FUTURE_SCHEDULED, habitBaseColor, showFutureScheduledIndicator = true), // Future Scheduled
        15 to CalendarDayUIData(CalendarDayStatus.GOAL_MET, habitColors.primary)
    )

    CalendarSection(
        currentMonth = YearMonth.now(),
        markedDays = sampleMarkedDays,
        habitColors = habitColors,
        onPreviousMonth = {},
        onNextMonth = {},
        onDateSelected = { day -> println("Date selected: $day") }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DateCellVariousStatesPreview() {
    val habitBaseColor = Color(0xFF2196F3) // Blue for this preview
    val habitColors = generateHabitInformationColors(habitBaseColor)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
        DateCell(day = 1, dayUiData = CalendarDayUIData(CalendarDayStatus.GOAL_MET, habitColors.primary), habitColors = habitColors, onDateSelected = {})
        DateCell(day = 2, dayUiData = CalendarDayUIData(CalendarDayStatus.PARTIALLY_MET, habitColors.primaryLight), habitColors = habitColors, onDateSelected = {})
        DateCell(day = 3, dayUiData = CalendarDayUIData(CalendarDayStatus.OVERDONE, habitColors.primary, showOverdoneIndicator = true), habitColors = habitColors, onDateSelected = {})
        DateCell(day = 4, dayUiData = CalendarDayUIData(CalendarDayStatus.MISSED, Color.LightGray.copy(alpha = 0.6f)), habitColors = habitColors, onDateSelected = {})
        DateCell(day = 5, dayUiData = CalendarDayUIData(CalendarDayStatus.NOT_SCHEDULED, Color.Transparent), habitColors = habitColors, onDateSelected = {})
        DateCell(day = 6, dayUiData = CalendarDayUIData(CalendarDayStatus.FUTURE_SCHEDULED, habitBaseColor, showFutureScheduledIndicator = true), habitColors = habitColors, onDateSelected = {})
    }
}

