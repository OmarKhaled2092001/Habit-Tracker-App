package com.example.habittrackerapp.ui.screens.habit_information

import androidx.compose.ui.graphics.Color

// Enum to represent the detailed status of a habit on a specific day.
// This will be used by the ViewModel to provide data to the CalendarSection.
enum class CalendarDayStatus {
    GOAL_MET,          // Habit goal achieved
    PARTIALLY_MET,   // Progress made, but goal not fully achieved
    OVERDONE,          // Habit goal exceeded
    MISSED,            // Habit scheduled but not done
    NOT_SCHEDULED,     // Habit not scheduled for this day
    FUTURE_SCHEDULED,  // A future day where the habit is scheduled
    NONE               // Default for days not in focus or without specific status
}

data class CalendarDayUIData(
    val status: CalendarDayStatus,
    val displayColor: Color,       // The color to use for marking the day, determined by ViewModel
    val showOverdoneIndicator: Boolean = false, // True if an 'overdone' indicator should be shown
    val showFutureScheduledIndicator: Boolean = false // True if a 'future scheduled' indicator (e.g., border) should be shown
)

