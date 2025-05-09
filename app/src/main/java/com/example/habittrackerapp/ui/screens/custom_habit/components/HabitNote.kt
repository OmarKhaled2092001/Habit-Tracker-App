package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitNote(
    note: String, // Receive note value from parent (ViewModel)
    onNoteChanged: (String) -> Unit, // Callback to parent (ViewModel)
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Text(
            text = "NOTE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = note, // Use value from parameter
            onValueChange = onNoteChanged, // Directly use the callback
            placeholder = { Text("Description or other info", color = Color.Gray) }, // Use material3 Text
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors( // Use material3 colors
                focusedIndicatorColor = Color(0xFF00C853),
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitNotePreview() {
    // Preview needs explicit value
    HabitNote(
        note = "This is a sample note.",
        onNoteChanged = { }
    )
}

