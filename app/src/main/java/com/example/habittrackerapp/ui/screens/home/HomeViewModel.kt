package com.example.habittrackerapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.HabitItem
import com.example.habittrackerapp.ui.screens.home.components.fetchFullName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class HomeViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _fullName = MutableStateFlow<String?>(null)
    val fullName: StateFlow<String?> = _fullName.asStateFlow()

    private val _habits = MutableStateFlow<List<HabitItem>>(emptyList())
    val habits: StateFlow<List<HabitItem>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun fetchUserFullName() {
        viewModelScope.launch {
            _isLoading.value = true
            fetchFullName { name ->
                _fullName.value = name
                _isLoading.value = false
            }
        }
    }

    fun fetchHabits() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val snapshot = Firebase.firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val habitsList = snapshot.get("habits") as? List<Map<String, Any>>
                    val parsedHabits = habitsList?.map {
                        HabitItem(
                            name = it["name"] as? String ?: "",
                            emoji = it["emoji"] as? String ?: ""
                        )
                    } ?: emptyList()

                    _habits.value = parsedHabits
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteHabit(habit: HabitItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val userDocRef = Firebase.firestore.collection("users").document(userId)
                    val snapshot = userDocRef.get().await()

                    val currentHabits = snapshot.get("habits") as? List<Map<String, Any>> ?: emptyList()
                    val updatedHabits = currentHabits.filterNot {
                        it["name"] == habit.name && it["emoji"] == habit.emoji
                    }

                    userDocRef.update("habits", updatedHabits).await()
                    fetchHabits()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun addHabitToFirestore(userId: String, habitName: String, emoji: String): AddHabitResult {
        return try {
            val userDocRef = Firebase.firestore.collection("users").document(userId)

            val snapshot = userDocRef.get().await()
            val currentHabits = snapshot.get("habits") as? List<Map<String, Any>> ?: emptyList()

            val habitExists = currentHabits.any {
                (it["name"] as? String).equals(habitName, ignoreCase = true)
            }

            if (habitExists) {
                AddHabitResult.AlreadyExists
            } else {
                val newHabit = mapOf(
                    "name" to habitName,
                    "emoji" to emoji
                )
                val updatedHabits = currentHabits + newHabit
                userDocRef.update("habits", updatedHabits).await()
                fetchHabits()
                AddHabitResult.Success
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AddHabitResult.Failed
        }
    }
}


enum class AddHabitResult {
    Success,
    AlreadyExists,
    Failed
}
