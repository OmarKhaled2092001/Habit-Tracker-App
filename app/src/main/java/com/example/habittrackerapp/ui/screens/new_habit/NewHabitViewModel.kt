package com.example.habittrackerapp.ui.screens.new_habit

import android.util.Log // For logging errors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb // To convert Color to Int
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.HabitInfo
import com.example.habittrackerapp.data.habits // Assuming habits data is here
import com.example.habittrackerapp.utils.HabitColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Use await for cleaner async
import java.text.SimpleDateFormat // For date formatting
import java.util.Date // For current date
import java.util.Locale // For date formatting locale

data class NewHabitUiState(
    val popularHabits: List<HabitInfo> = emptyList(),
    val selectedHabits: Set<HabitInfo> = emptySet(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)

class NewHabitScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NewHabitUiState())
    val uiState: StateFlow<NewHabitUiState> = _uiState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadPopularHabits()
    }

    private fun loadPopularHabits() {

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(popularHabits = habits)
            }
        }
    }

    fun toggleHabitSelection(habit: HabitInfo) {
        _uiState.update { currentState ->
            val currentSelected = currentState.selectedHabits
            val newSelected = if (currentSelected.contains(habit)) {
                currentSelected - habit
            } else {
                currentSelected + habit
            }
            currentState.copy(selectedHabits = newSelected)
        }
    }

    fun getHabitColor(index: Int): Color {
        return HabitColors.colorOptions[index % HabitColors.colorOptions.size]
    }

    fun saveSelectedHabitsToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(saveError = "User not logged in", isSaving = false) }
            return
        }

        if (_uiState.value.selectedHabits.isEmpty()) {
            Log.d("FirestoreSave", "No habits selected to save.")
            _uiState.update { it.copy(saveSuccess = true, isSaving = false) }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveError = null, saveSuccess = false) }


        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val currentDateString = dateFormat.format(Date())


        val habitsToSave = _uiState.value.selectedHabits.map { habit ->
            mapOf(
                "habitName" to habit.habitName,
                "selectedIcon" to habit.selectedIcon,
                "selectedColor" to habit.selectedColor.toArgb(),
                "goalCount" to habit.goalCount,
                "unit" to habit.unit,
                "frequency" to habit.frequency,
                "selectedDays" to habit.selectedDays,
                "reminders" to habit.reminders.map { reminder ->
                    mapOf(
                        "title" to reminder.title,
                        "time" to reminder.time,
                        "frequency" to reminder.frequency,
                        "selectedDays" to reminder.selectedDays,
                        "isEnabled" to reminder.isEnabled
                    )
                },
                "note" to habit.note,
                "startDate" to currentDateString,
                "endDate" to habit.endDate
            )
        }

        val userDocRef = firestore.collection("users").document(userId)

        viewModelScope.launch {
            try {
                userDocRef.set(mapOf("selectedHabitsFullInfo" to habitsToSave), com.google.firebase.firestore.SetOptions.merge()).await()
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                Log.d("FirestoreSave", "Selected habits (full info with current start date) saved successfully for user $userId")
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveError = e.message ?: "Failed to save habits") }
                Log.e("FirestoreSave", "Error saving habits (full info with current start date) for user $userId", e)
            }
        }
    }


    fun resetSaveStatus() {
        _uiState.update { it.copy(saveError = null, saveSuccess = false) }
    }

}

