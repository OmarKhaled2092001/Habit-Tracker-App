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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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


@Composable
fun HabitPeriod(
    startDate: String, // Receive formatted date string from ViewModel
    endDate: String,   // Receive formatted date string from ViewModel
    onStartDateSelected: (Long?) -> Unit, // Callback with selected millis
    onEndDateSelected: (Long?) -> Unit,   // Callback with selected millis
    modifier: Modifier = Modifier
) {
    // Local state to control the visibility of the date pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SectionTitle(text = "HABIT PERIOD")
        Spacer(modifier = Modifier.height(8.dp))
        PeriodCard {
            DateSelector(
                label = "Start day",
                // Display the formatted date from ViewModel
                date = startDate.ifEmpty { "Select Start Date" }, // Provide placeholder
                onClick = { showStartDatePicker = true } // Show the picker on click
            )
            Spacer(modifier = Modifier.height(16.dp))
            DateSelector(
                label = "End day",
                // Display the formatted date from ViewModel
                date = endDate.ifEmpty { "Select End Date" }, // Provide placeholder
                onClick = { showEndDatePicker = true } // Show the picker on click
            )
        }
    }

    // Show Start Date Picker Dialog when showStartDatePicker is true
    if (showStartDatePicker) {
        DatePickerModal(
            onDateSelected = onStartDateSelected, // Pass the callback to the dialog
            onDismiss = { showStartDatePicker = false } // Hide dialog on dismiss
        )
    }

    // Show End Date Picker Dialog when showEndDatePicker is true
    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = onEndDateSelected, // Pass the callback to the dialog
            onDismiss = { showEndDatePicker = false } // Hide dialog on dismiss
        )
    }
}

// Renamed DatePickerModalInput to DatePickerModal for clarity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, // Callback with selected millis
    onDismiss: () -> Unit
) {
    // Initialize DatePickerState, potentially with initial date if needed
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Pass the selected date in milliseconds to the callback
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss() // Dismiss the dialog
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { // Dismiss the dialog on cancel
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState) // The actual DatePicker composable
    }
}

// SectionTitle, PeriodCard, DateSelector remain unchanged as they are pure UI components
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun PeriodCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun DateSelector(
    label: String,
    date: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = date, // Display the provided date string
                fontSize = 16.sp,
                color = if (date.startsWith("Select")) Color.Gray else Color.Black, // Use gray for placeholder
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitPeriodPreview() {
    // Preview needs explicit values
    HabitPeriod(
        startDate = "Monday, May 5",
        endDate = "",
        onStartDateSelected = { },
        onEndDateSelected = { }
    )
}

