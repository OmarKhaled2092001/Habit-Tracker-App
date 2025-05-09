package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.custom_habit.Reminder // Import Reminder data class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (title: String, time: String, frequency: String, selectedDays: List<String>) -> Unit,
    initialReminder: Reminder? = null // Optional initial data for editing
) {
    // Local state for the bottom sheet inputs
    var reminderTitle by remember { mutableStateOf(initialReminder?.title ?: "") }
    var selectedTime by remember { mutableStateOf(initialReminder?.time ?: "8:00 AM") }
    var frequency by remember { mutableStateOf(initialReminder?.frequency ?: "Daily") }
    // Ensure days are correctly initialized based on frequency
    var selectedDays by remember { mutableStateOf(initialReminder?.selectedDays ?: if (frequency == "Daily") listOf("Mon", "Tue", "Wed", "Thu", "Sun") else emptyList()) }

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
                title = if (initialReminder == null) "New Reminder" else "Edit Reminder",
                subtitle = "SET YOUR REMINDER",
                onCancel = onDismiss,
                onSave = {
                    // Pass the current state to the save callback
                    onSave(reminderTitle, selectedTime, frequency, selectedDays)
                    // Dismissal is handled by the caller after saving
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithCounter(
                value = reminderTitle,
                onValueChange = { reminderTitle = it },
                label = "Reminder",
                maxChars = 100, // Consider making this a constant
                singleLine = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            FrequencySelection(
                frequency = frequency,
                onFrequencyChange = { newFrequency ->
                    frequency = newFrequency
                    // Reset days if switching to Daily, or clear if switching from Daily
                    if (newFrequency == "Daily") {
                        selectedDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    } else if (frequency == "Daily") { // Check previous frequency
                        selectedDays = emptyList() // Or keep previous weekly selection?
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
            TimeSelection(
                selectedTime = selectedTime,
                onTimeSelected = { selectedTime = it }
            )
        }
    }
}


@Composable
private fun TimeSelection(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "NOTIFY AT",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { showTimePicker = true }
                .height(60.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedTime,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        if (showTimePicker) {
            TimePickerDialog(
                initialTime = selectedTime,
                onTimeSelected = {
                    onTimeSelected(it)
                    showTimePicker = false // Dismiss picker after selection
                },
                onDismiss = { showTimePicker = false },
                title = "Select Reminder Time"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ReminderBottomSheetPreview() {
    ReminderBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismiss = { },
        onSave = { _, _, _, _ -> }
    )
}

