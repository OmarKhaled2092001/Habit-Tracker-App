package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun FrequencySelection(
    frequency: String,
    onFrequencyChange: (String) -> Unit
) {
    Text(
        text = "REPEAT",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FrequencyButton(
            text = "DAILY",
            isSelected = frequency == "Daily",
            onClick = { onFrequencyChange("Daily") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        FrequencyButton(
            text = "WEEKLY",
            isSelected = frequency == "Weekly",
            onClick = { onFrequencyChange("Weekly") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FrequencyButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .background(
                if (isSelected) Color(0xFF2962FF) else Color.White,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun DaysSelection(
    selectedDays: List<String>,
    onDaysChange: (List<String>) -> Unit
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Text(
        text = "ON THESE DAYS",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            DayButton(
                day = day,
                isSelected = selectedDays.contains(day),
                onClick = {
                    onDaysChange(
                        if (selectedDays.contains(day)) selectedDays - day
                        else selectedDays + day
                    )
                }
            )
        }
    }
}

@Composable
private fun DayButton(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                if (isSelected) Color(0xFF2962FF) else Color.White,
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FrequencySelectionPreview() {
    FrequencySelection(
        frequency = "Daily",
        onFrequencyChange = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DaysSelectionPreview() {
    DaysSelection(
        selectedDays = listOf("Mon", "Tue", "Wed"),
        onDaysChange = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DayButtonPreview() {
    DayButton(
        day = "Mon",
        isSelected = true,
        onClick = {}
    )
}