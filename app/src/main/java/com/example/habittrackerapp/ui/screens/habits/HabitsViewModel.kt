package com.example.habittrackerapp.ui.screens.habits

import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.data.HabitItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HabitsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _selectedHabits = MutableStateFlow<List<HabitItem>>(emptyList())
    val selectedHabits: StateFlow<List<HabitItem>> = _selectedHabits

    fun addHabitToSelection(habit: HabitItem) {
        _selectedHabits.value = _selectedHabits.value + habit
    }

    fun removeHabitFromSelection(habit: HabitItem) {
        _selectedHabits.value = _selectedHabits.value - habit
    }

    fun saveSelectedHabits(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userDoc = firestore.collection("users").document(userId)
            val habitsData = _selectedHabits.value.map { habit ->
                mapOf(
                    "name" to habit.name,
                    "emoji" to habit.emoji
                )
            }
            userDoc.update("habits", habitsData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        }
    }
}
