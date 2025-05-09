package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitNameInput(
    habitName: String,
    onNameChange: (String) -> Unit,
    maxChars: Int
) {
    Column {
        Text(
            text = "NAME",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextFieldWithCounter(
            value = habitName,
            onValueChange = onNameChange,
            label = "Enter habit name",
            maxChars = maxChars
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitNameInputPreview() {
    HabitNameInput(habitName = "", onNameChange = {}, maxChars = 40)
}