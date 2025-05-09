//package com.example.habittrackerapp.ui.screens.custom_habit
//
//import android.util.Log
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.toArgb
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import java.util.UUID
//
//// Reminder data class definition
//data class Reminder(
//    val id: String = UUID.randomUUID().toString(), // Unique ID for stable updates/deletes
//    val title: String,
//    val time: String,
//    val frequency: String, // Store raw frequency
//    val selectedDays: List<String>, // Store selected days
//    val isEnabled: Boolean = true
//) {
//    // Computed property for display schedule string
//    val schedule: String
//        get() = if (frequency == "Daily") {
//            "Every day"
//        } else {
//            if (selectedDays.isEmpty()) "Weekly (No days selected)" else "Every ${selectedDays.joinToString()}"
//        }
//}
//
//// State holder data class
//data class CustomHabitState(
//    val habitName: String = "",
//    val selectedIcon: String = "💡", // Default icon
//    val selectedColor: Color = Color(0xFF6db5dd), // Default color Sky Blue
//    val goalCount: String = "1", // Default goal
//    val unit: String = "Time", // Default unit
//    val frequency: String = "Daily",
//    val selectedDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"), // Default to all days for Daily
//    val reminders: List<Reminder> = emptyList(),
//    val note: String = "",
//    val startDate: String = "", // Will be set dynamically on save
//    val endDate: String = "",
//    val iconSearchText: String = "",
//    // Save states
//    val isSaving: Boolean = false,
//    val saveError: String? = null,
//    val saveSuccess: Boolean = false
//)
//
//class CustomHabitScreenViewModel : ViewModel() {
//
//    private val _habitState = MutableStateFlow(CustomHabitState())
//    val habitState: StateFlow<CustomHabitState> = _habitState.asStateFlow()
//
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    private val maxHabitNameChars = 40
//    // Use dd/MM/yyyy format for consistency with previous implementation
//    private val saveDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    // Keep display format separate if needed
//    private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
//
//    // --- Event Handlers ---
//
//    fun updateHabitName(newName: String) {
//        if (newName.length <= maxHabitNameChars) {
//            _habitState.update { it.copy(habitName = newName) }
//        }
//    }
//
//    fun updateSelectedIcon(newIcon: String) {
//        _habitState.update { it.copy(selectedIcon = newIcon) }
//    }
//
//    fun updateSelectedColor(newColor: Color) {
//        _habitState.update { it.copy(selectedColor = newColor) }
//    }
//
//    fun updateGoal(goalCount: String, unit: String, frequency: String, days: List<String>) {
//        _habitState.update { currentState ->
//            val updatedDays = if (frequency == "Daily") {
//                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//            } else {
//                days
//            }
//            currentState.copy(
//                goalCount = goalCount,
//                unit = unit,
//                frequency = frequency,
//                selectedDays = updatedDays
//            )
//        }
//    }
//
//    fun addReminder(title: String, time: String, frequency: String, selectedDays: List<String>) {
//        val newReminder = Reminder(
//            title = title,
//            time = time,
//            frequency = frequency,
//            selectedDays = if (frequency == "Daily") listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") else selectedDays,
//            isEnabled = true
//        )
//        _habitState.update {
//            it.copy(reminders = it.reminders + newReminder)
//        }
//    }
//
//    fun updateReminder(
//        reminderId: String,
//        isEnabled: Boolean? = null,
//        title: String? = null,
//        time: String? = null,
//        frequency: String? = null,
//        selectedDays: List<String>? = null
//    ) {
//        _habitState.update { currentState ->
//            val updatedReminders = currentState.reminders.map { reminder ->
//                if (reminder.id == reminderId) {
//                    val finalFrequency = frequency ?: reminder.frequency
//                    reminder.copy(
//                        isEnabled = isEnabled ?: reminder.isEnabled,
//                        title = title ?: reminder.title,
//                        time = time ?: reminder.time,
//                        frequency = finalFrequency,
//                        selectedDays = if (finalFrequency == "Daily") {
//                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//                        } else {
//                            selectedDays ?: reminder.selectedDays
//                        }
//                    )
//                } else {
//                    reminder
//                }
//            }
//            currentState.copy(reminders = updatedReminders)
//        }
//    }
//
//    fun deleteReminder(reminderId: String) {
//        _habitState.update { currentState ->
//            currentState.copy(reminders = currentState.reminders.filterNot { it.id == reminderId })
//        }
//    }
//
//    fun updateNote(newNote: String) {
//        _habitState.update { it.copy(note = newNote) }
//    }
//
//    // Update state with display format
//    fun updateStartDate(millis: Long?) {
//        val formattedDate = millis?.let { displayDateFormat.format(Date(it)) } ?: ""
//        _habitState.update { it.copy(startDate = formattedDate) }
//    }
//
//    // Update state with display format
//    fun updateEndDate(millis: Long?) {
//        val formattedDate = millis?.let { displayDateFormat.format(Date(it)) } ?: ""
//        _habitState.update { it.copy(endDate = formattedDate) }
//    }
//
//    fun updateIconSearchText(text: String) {
//        _habitState.update { it.copy(iconSearchText = text) }
//    }
//
//    // Function to save the currently configured custom habit
//    fun saveCustomHabitToFirestore() {
//        val userId = auth.currentUser?.uid
//        if (userId == null) {
//            _habitState.update { it.copy(saveError = "User not logged in", isSaving = false) }
//            return
//        }
//
//        // Basic validation (e.g., habit name cannot be empty)
//        if (_habitState.value.habitName.isBlank()) {
//            _habitState.update { it.copy(saveError = "Habit name cannot be empty", isSaving = false) }
//            return
//        }
//
//        _habitState.update { it.copy(isSaving = true, saveError = null, saveSuccess = false) }
//
//        // Get current date formatted as dd/MM/yyyy
//        val currentDateString = saveDateFormat.format(Date())
//
//        // Create the habit map to save, using current state
//        val habitToSave = mapOf(
//            "habitName" to _habitState.value.habitName,
//            "selectedIcon" to _habitState.value.selectedIcon,
//            "selectedColor" to _habitState.value.selectedColor.toArgb(),
//            "goalCount" to _habitState.value.goalCount,
//            "unit" to _habitState.value.unit,
//            "frequency" to _habitState.value.frequency,
//            "selectedDays" to _habitState.value.selectedDays,
//            "reminders" to _habitState.value.reminders.map { reminder ->
//                mapOf(
//                    "title" to reminder.title,
//                    "time" to reminder.time,
//                    "frequency" to reminder.frequency,
//                    "selectedDays" to reminder.selectedDays,
//                    "isEnabled" to reminder.isEnabled
//                    // Exclude reminder.id as it's likely not needed in Firestore
//                )
//            },
//            "note" to _habitState.value.note,
//            "startDate" to currentDateString, // Use current date
//            "endDate" to _habitState.value.endDate // Use user-selected end date (if any)
//        )
//
//        val userDocRef = firestore.collection("users").document(userId)
//
//        viewModelScope.launch {
//            try {
//                // Add the new custom habit to the existing list using FieldValue.arrayUnion
//                // This prevents duplicates if the exact same habit map is added again,
//                // and safely adds to the list even if the field doesn't exist yet.
//                userDocRef.update("selectedHabitsFullInfo", FieldValue.arrayUnion(habitToSave)).await()
//
//                _habitState.update { it.copy(isSaving = false, saveSuccess = true) }
//                Log.d("FirestoreSave", "Custom habit saved successfully for user $userId")
//            } catch (e: Exception) {
//                _habitState.update { it.copy(isSaving = false, saveError = e.message ?: "Failed to save custom habit") }
//                Log.e("FirestoreSave", "Error saving custom habit for user $userId", e)
//            }
//        }
//    }
//
//    // Function to reset save status after UI handles it
//    fun resetSaveStatus() {
//        _habitState.update { it.copy(saveError = null, saveSuccess = false) }
//    }
//}


package com.example.habittrackerapp.ui.screens.custom_habit

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String,
    val frequency: String,
    val selectedDays: List<String>,
    val isEnabled: Boolean = true
) {

    val schedule: String
        get() = if (frequency == "Daily") {
            "Every day"
        } else {
            if (selectedDays.isEmpty()) "Weekly (No days selected)" else "Every ${selectedDays.joinToString()}"
        }
}


data class CustomHabitState(
    val habitName: String = "",
    val selectedIcon: String = "💡",
    val selectedColor: Color = Color(0xFF6db5dd),
    val goalCount: String = "1",
    val unit: String = "Time",
    val frequency: String = "Daily",
    val selectedDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val reminders: List<Reminder> = emptyList(),
    val note: String = "",
    val displayStartDate: String = "",
    val saveableStartDate: String = "",
    val displayEndDate: String = "",
    val saveableEndDate: String = "",
    val iconSearchText: String = "",

    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)

class CustomHabitScreenViewModel : ViewModel() {

    private val _habitState = MutableStateFlow(CustomHabitState())
    val habitState: StateFlow<CustomHabitState> = _habitState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val maxHabitNameChars = 40

    private val firestoreDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())



    fun updateHabitName(newName: String) {
        if (newName.length <= maxHabitNameChars) {
            _habitState.update { it.copy(habitName = newName) }
        }
    }

    fun updateSelectedIcon(newIcon: String) {
        _habitState.update { it.copy(selectedIcon = newIcon) }
    }

    fun updateSelectedColor(newColor: Color) {
        _habitState.update { it.copy(selectedColor = newColor) }
    }

    fun updateGoal(goalCount: String, unit: String, frequency: String, days: List<String>) {
        _habitState.update { currentState ->
            val updatedDays = if (frequency == "Daily") {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            } else {
                days
            }
            currentState.copy(
                goalCount = goalCount,
                unit = unit,
                frequency = frequency,
                selectedDays = updatedDays
            )
        }
    }

    fun addReminder(title: String, time: String, frequency: String, selectedDays: List<String>) {
        val newReminder = Reminder(
            title = title,
            time = time,
            frequency = frequency,
            selectedDays = if (frequency == "Daily") listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") else selectedDays,
            isEnabled = true
        )
        _habitState.update {
            it.copy(reminders = it.reminders + newReminder)
        }
    }

    fun updateReminder(
        reminderId: String,
        isEnabled: Boolean? = null,
        title: String? = null,
        time: String? = null,
        frequency: String? = null,
        selectedDays: List<String>? = null
    ) {
        _habitState.update { currentState ->
            val updatedReminders = currentState.reminders.map { reminder ->
                if (reminder.id == reminderId) {
                    val finalFrequency = frequency ?: reminder.frequency
                    reminder.copy(
                        isEnabled = isEnabled ?: reminder.isEnabled,
                        title = title ?: reminder.title,
                        time = time ?: reminder.time,
                        frequency = finalFrequency,
                        selectedDays = if (finalFrequency == "Daily") {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        } else {
                            selectedDays ?: reminder.selectedDays
                        }
                    )
                } else {
                    reminder
                }
            }
            currentState.copy(reminders = updatedReminders)
        }
    }

    fun deleteReminder(reminderId: String) {
        _habitState.update { currentState ->
            currentState.copy(reminders = currentState.reminders.filterNot { it.id == reminderId })
        }
    }

    fun updateNote(newNote: String) {
        _habitState.update { it.copy(note = newNote) }
    }

    fun updateStartDate(millis: Long?) {
        val displayDate = millis?.let { displayDateFormat.format(Date(it)) } ?: ""
        val saveableDate = millis?.let { firestoreDateFormat.format(Date(it)) } ?: ""
        _habitState.update { it.copy(displayStartDate = displayDate, saveableStartDate = saveableDate) }
    }

    fun updateEndDate(millis: Long?) {
        val displayDate = millis?.let { displayDateFormat.format(Date(it)) } ?: ""
        val saveableDate = millis?.let { firestoreDateFormat.format(Date(it)) } ?: ""
        _habitState.update { it.copy(displayEndDate = displayDate, saveableEndDate = saveableDate) }
    }

    fun updateIconSearchText(text: String) {
        _habitState.update { it.copy(iconSearchText = text) }
    }

    fun saveCustomHabitToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _habitState.update { it.copy(saveError = "User not logged in", isSaving = false) }
            return
        }

        if (_habitState.value.habitName.isBlank()) {
            _habitState.update { it.copy(saveError = "Habit name cannot be empty", isSaving = false) }
            return
        }

        _habitState.update { it.copy(isSaving = true, saveError = null, saveSuccess = false) }

        val finalStartDate = if (_habitState.value.saveableStartDate.isNotBlank()) {
            _habitState.value.saveableStartDate
        } else {
            firestoreDateFormat.format(Date()) // Default to current date if not selected, in MM/dd/yyyy
        }

        val habitToSave = mapOf(
            "habitName" to _habitState.value.habitName,
            "selectedIcon" to _habitState.value.selectedIcon,
            "selectedColor" to _habitState.value.selectedColor.toArgb(),
            "goalCount" to _habitState.value.goalCount,
            "unit" to _habitState.value.unit,
            "frequency" to _habitState.value.frequency,
            "selectedDays" to _habitState.value.selectedDays,
            "reminders" to _habitState.value.reminders.map { reminder ->
                mapOf(
                    "title" to reminder.title,
                    "time" to reminder.time,
                    "frequency" to reminder.frequency,
                    "selectedDays" to reminder.selectedDays,
                    "isEnabled" to reminder.isEnabled
                )
            },
            "note" to _habitState.value.note,
            "startDate" to finalStartDate,
            "endDate" to _habitState.value.saveableEndDate
        )

        val userDocRef = firestore.collection("users").document(userId)

        viewModelScope.launch {
            try {
                userDocRef.update("selectedHabitsFullInfo", FieldValue.arrayUnion(habitToSave)).await()
                _habitState.update { it.copy(isSaving = false, saveSuccess = true) }
                Log.d("FirestoreSave", "Custom habit saved successfully for user $userId. Data: $habitToSave")
            } catch (e: Exception) {
                _habitState.update { it.copy(isSaving = false, saveError = e.message ?: "Failed to save custom habit") }
                Log.e("FirestoreSave", "Error saving custom habit for user $userId", e)
            }
        }
    }

    fun resetSaveStatus() {
        _habitState.update { it.copy(saveError = null, saveSuccess = false) }
    }
}

