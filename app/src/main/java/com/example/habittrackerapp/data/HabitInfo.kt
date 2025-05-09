package com.example.habittrackerapp.data

import androidx.compose.ui.graphics.Color

data class HabitInfo(
    val habitName: String,
    val selectedIcon: String,
    val selectedColor: Color,
    val goalCount: String,
    val unit: String,
    val frequency: String,
    val selectedDays: List<String>,
    val reminders: List<Reminder>,
    val note: String,
    val startDate: String,
    val endDate: String
)
