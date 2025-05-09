package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun DateNavigationBar(
    selectedDate: LocalDate,
    colors: HabitInformationColors,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()) }
    val displayDate = when {
        selectedDate.isEqual(LocalDate.now()) -> "TODAY"
        selectedDate.isEqual(LocalDate.now().minusDays(1)) -> "YESTERDAY"
        selectedDate.isEqual(LocalDate.now().plusDays(1)) -> "TOMORROW"
        else -> selectedDate.format(dateFormatter).uppercase(Locale.getDefault())
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Day",
                tint = colors.onPrimary
            )
        }
        Text(
            text = displayDate,
            color = colors.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = onNextDay) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Day",
                tint = colors.onPrimary
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF00796B)
@Composable
private fun DateNavigationBarPreview() {
    val previewColors = generateHabitInformationColors(Color(0xFF00796B))
    DateNavigationBar(
        selectedDate = LocalDate.now(),
        colors = previewColors,
        onPreviousDay = {},
        onNextDay = {}
    )
}