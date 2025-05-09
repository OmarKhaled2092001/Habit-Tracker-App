package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitGoal(
    modifier: Modifier = Modifier,
    // Receive state from the parent (ultimately from ViewModel)
    goalCount: String,
    unit: String,
    frequency: String,
    selectedDays: List<String>,
    // Callback to notify the parent (screen) to show the bottom sheet
    onChangeClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = "GOAL",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Pass the received state down to GoalSummaryCard
        GoalSummaryCard(
            goalCount = goalCount,
            unit = unit,
            frequency = frequency,
            selectedDays = selectedDays,
            // Trigger the callback when the change button is clicked
            onChangeClick = onChangeClick
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun HabitGoalPreview() {
    HabitGoal(
        goalCount = "5",
        unit = "times",
        frequency = "Weekly",
        selectedDays = listOf("Mon", "Wed", "Fri"),
        onChangeClick = {}
    )
}

