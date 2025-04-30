package com.example.habittrackerapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HorizontalDayPicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val weekDates = getWeekFromDate(selectedDate)

    LazyRow {
        items(weekDates) { date ->
            val isToday = date == LocalDate.now()
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .border(
                        width = 2.dp,
                        color = if (isToday) Color.Blue else Color(0xFFA5A6A9),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .width(50.dp)
                    .clickable { onDateSelected(date) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.Blue else Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
                    fontSize = 12.sp,
                    color = if (isToday) Color.Blue else Color.Gray
                )
            }
        }
    }
}

private fun getWeekFromDate(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.with(DayOfWeek.MONDAY)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}


@Preview(showBackground = true)
@Composable
private fun HorizontalDayPickerPreview() {
    HorizontalDayPicker(
        selectedDate = LocalDate.now(),
        onDateSelected = {}
    )
}