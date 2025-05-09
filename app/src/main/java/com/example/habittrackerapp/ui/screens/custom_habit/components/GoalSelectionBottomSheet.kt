package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSelectionBottomSheet(
    sheetState: SheetState,
    // Receive initial state from the caller (screen/ViewModel)
    initialGoalCount: String,
    initialUnit: String,
    initialFrequency: String,
    initialSelectedDays: List<String>,
    // Callback to pass the final selected values back to the caller
    onSave: (goalCount: String, unit: String, frequency: String, selectedDays: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    // Local state for the bottom sheet inputs, initialized with received values
    var goalCount by remember { mutableStateOf(initialGoalCount) }
    var unit by remember { mutableStateOf(initialUnit) }
    var frequency by remember { mutableStateOf(initialFrequency) }
    // Ensure days are correctly initialized based on frequency
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFEDEDF3),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.height(650.dp) // Consider dynamic height or wrap content
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BottomSheetHeader(
                title = "Habit Goal",
                subtitle = "SET YOUR GOAL",
                onCancel = onDismiss, // Use the provided dismiss callback
                onSave = {
                    // Pass the current local state to the save callback
                    onSave(goalCount, unit, frequency, selectedDays)
                    // Dismissal is handled by the caller after saving
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FrequencySelection(
                frequency = frequency,
                onFrequencyChange = { newFrequency ->
                    frequency = newFrequency
                    // Reset days if switching to Daily, or clear if switching from Daily
                    if (newFrequency == "Daily") {
                        selectedDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    }
                }
            )
            // Only show day selection if frequency is Weekly
            if (frequency == "Weekly") {
                Spacer(modifier = Modifier.height(16.dp))
                DaysSelection(
                    selectedDays = selectedDays,
                    onDaysChange = { selectedDays = it }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            GoalInput(
                goalCount = goalCount,
                unit = unit,
                onGoalCountChange = { goalCount = it },
                onUnitChange = { unit = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
private fun GoalInput(
    goalCount: String,
    unit: String,
    onGoalCountChange: (String) -> Unit,
    onUnitChange: (String) -> Unit
) {
    Text(
        text = "WITH A GOAL OF",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = goalCount,
            // Basic validation: only allow digits or empty string
            onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) onGoalCountChange(it) },
            placeholder = { Text("e.g., 10", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF00C853),
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f) // Use weight for flexible sizing
                .padding(end = 8.dp)
        )
        OutlinedTextField(
            value = unit,
            onValueChange = onUnitChange,
            placeholder = { Text("e.g., km, cups", color = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF00C853),
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun GoalSelectionBottomSheetPreview() {
    GoalSelectionBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        initialGoalCount = "5",
        initialUnit = "cups",
        initialFrequency = "Daily",
        initialSelectedDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu"),
        onSave = { _, _, _, _ -> },
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GoalInputPreview() {
    GoalInput(
        goalCount = "5",
        unit = "cups",
        onGoalCountChange = {},
        onUnitChange = {}
    )
}

