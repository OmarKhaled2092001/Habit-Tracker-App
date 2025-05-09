package com.example.habittrackerapp.data

import java.time.LocalDate

// Data class to represent a single day's progress for a habit
data class HabitDailyProgress(
    var date: LocalDate = LocalDate.now(), // Default to now, will be overwritten by Firestore data
    val actualCount: Int? = null,
    val actualDurationMillis: Long? = null,
    val goalMet: Boolean = false, // Default to false
    val goalExceeded: Boolean = false // Default to false
)

// Represents the full progress history for a specific habit
data class HabitProgressHistory(
    val habitName: String = "", // Default to empty string
    val dailyEntries: List<HabitDailyProgress> = emptyList()
)

