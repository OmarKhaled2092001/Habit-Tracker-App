package com.example.habittrackerapp.data

data class Reminder(
    val title: String,
    val time: String,
    val frequency: String,
    val selectedDays: List<String>,
    val isEnabled: Boolean
)
