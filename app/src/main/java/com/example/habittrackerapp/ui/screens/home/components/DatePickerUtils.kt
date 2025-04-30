package com.example.habittrackerapp.ui.screens.home.components

import android.content.Context
import java.time.LocalDate

fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        },
        year,
        month,
        day
    ).show()
}