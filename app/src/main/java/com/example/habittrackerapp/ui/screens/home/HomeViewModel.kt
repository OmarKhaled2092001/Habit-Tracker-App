package com.example.habittrackerapp.ui.screens.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.HabitInfo
import com.example.habittrackerapp.data.Reminder
import com.example.habittrackerapp.ui.screens.home.components.fetchFullName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _fullName = MutableStateFlow<String?>(null)
    val fullName: StateFlow<String?> = _fullName.asStateFlow()

    private val _habits = MutableStateFlow<List<HabitInfo>>(emptyList())
    val habits: StateFlow<List<HabitInfo>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

//    private val firestoreDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        // fetchHabits() // Uncomment if fetching is needed on date change
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
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("HomeViewModel", "User not logged in, cannot fetch habits.")
            _habits.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userDocRef = firestore.collection("users").document(userId)
                val snapshot = userDocRef.get().await()
                val habitsList = snapshot.get("selectedHabitsFullInfo") as? List<Map<String, Any>>

                val parsedHabits = habitsList?.mapNotNull { habitMap ->
                    try {
                        val habitName = habitMap["habitName"] as? String ?: ""
                        val selectedIcon = habitMap["selectedIcon"] as? String ?: ""
                        val colorInt = (habitMap["selectedColor"] as? Long)?.toInt() ?: Color.Gray.toArgb()
                        val selectedColor = Color(colorInt)
                        val goalCount = habitMap["goalCount"] as? String ?: "1"
                        val unit = habitMap["unit"] as? String ?: "Time"
                        val frequency = habitMap["frequency"] as? String ?: "Daily"
                        val selectedDays = habitMap["selectedDays"] as? List<String> ?: emptyList()
                        val note = habitMap["note"] as? String ?: ""
                        val startDate = habitMap["startDate"] as? String ?: ""
                        val endDate = habitMap["endDate"] as? String ?: ""
                        val remindersList = habitMap["reminders"] as? List<Map<String, Any>> ?: emptyList()
                        val parsedReminders = remindersList.mapNotNull { reminderMap ->
                            try {
                                Reminder(
                                    title = reminderMap["title"] as? String ?: "Reminder",
                                    time = reminderMap["time"] as? String ?: "09:00",
                                    frequency = reminderMap["frequency"] as? String ?: "Daily",
                                    selectedDays = reminderMap["selectedDays"] as? List<String> ?: emptyList(),
                                    isEnabled = reminderMap["isEnabled"] as? Boolean ?: true
                                )
                            } catch (e: Exception) {
                                Log.e("HomeViewModel", "Error parsing reminder: ${e.message}", e)
                                null
                            }
                        }
                        HabitInfo(
                            habitName = habitName,
                            selectedIcon = selectedIcon,
                            selectedColor = selectedColor,
                            goalCount = goalCount,
                            unit = unit,
                            frequency = frequency,
                            selectedDays = selectedDays,
                            reminders = parsedReminders,
                            note = note,
                            startDate = startDate,
                            endDate = endDate
                        )
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error parsing habit: ${e.message}", e)
                        null
                    }
                } ?: emptyList()

                _habits.value = parsedHabits
                Log.d("HomeViewModel", "Fetched ${parsedHabits.size} habits for user $userId")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching habits: ${e.message}", e)
                _habits.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteHabit(habitToDelete: HabitInfo) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("HomeViewModel", "User not logged in, cannot delete habit.")
            return
        }
        if (habitToDelete.habitName.isBlank()) {
            Log.w("HomeViewModel", "Habit name is blank, cannot delete habit progress.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Delete the habit from the 'selectedHabitsFullInfo' array in the user's document
                val userDocRef = firestore.collection("users").document(userId)
                val habitMapToDelete = mapOf(
                    "habitName" to habitToDelete.habitName,
                    "selectedIcon" to habitToDelete.selectedIcon,
                    "selectedColor" to habitToDelete.selectedColor.toArgb(),
                    "goalCount" to habitToDelete.goalCount,
                    "unit" to habitToDelete.unit,
                    "frequency" to habitToDelete.frequency,
                    "selectedDays" to habitToDelete.selectedDays,
                    "reminders" to habitToDelete.reminders.map {
                        mapOf(
                            "title" to it.title,
                            "time" to it.time,
                            "frequency" to it.frequency,
                            "selectedDays" to it.selectedDays,
                            "isEnabled" to it.isEnabled
                        )
                    },
                    "note" to habitToDelete.note,
                    "startDate" to habitToDelete.startDate,
                    "endDate" to habitToDelete.endDate
                )
                userDocRef.update("selectedHabitsFullInfo", FieldValue.arrayRemove(habitMapToDelete)).await()
                Log.d("HomeViewModel", "Successfully removed habit '${habitToDelete.habitName}' from user's list.")

                // 2. Delete all documents in the 'dailyEntries' subcollection
                val dailyEntriesRef = firestore.collection("users").document(userId)
                    .collection("habitProgress").document(habitToDelete.habitName)
                    .collection("dailyEntries")

                val dailyEntriesSnapshot = dailyEntriesRef.get().await()
                if (!dailyEntriesSnapshot.isEmpty) {
                    val batch = firestore.batch()
                    dailyEntriesSnapshot.documents.forEach { document ->
                        batch.delete(document.reference)
                    }
                    batch.commit().await()
                    Log.d("HomeViewModel", "Successfully deleted all daily entries for habit '${habitToDelete.habitName}'.")
                } else {
                    Log.d("HomeViewModel", "No daily entries found for habit '${habitToDelete.habitName}' to delete.")
                }

                // 3. Delete the habit's progress document itself ('habitProgress/{habitName}')
                val habitProgressDocRef = firestore.collection("users").document(userId)
                    .collection("habitProgress").document(habitToDelete.habitName)

                // Check if the document exists before attempting to delete it
                val habitProgressDocSnapshot = habitProgressDocRef.get().await()
                if (habitProgressDocSnapshot.exists()) {
                    habitProgressDocRef.delete().await()
                    Log.d("HomeViewModel", "Successfully deleted habit progress document for '${habitToDelete.habitName}'.")
                } else {
                    Log.d("HomeViewModel", "Habit progress document for '${habitToDelete.habitName}' does not exist, no need to delete.")
                }

                Log.d("HomeViewModel", "Successfully deleted habit '${habitToDelete.habitName}' and all its associated progress data.")

                // 4. Fetch the updated list of habits
                fetchHabits()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting habit '${habitToDelete.habitName}' or its progress: ${e.message}", e)
                fetchHabits()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun addPopularHabitToFirestore(userId: String, habitInfo: HabitInfo): AddHabitResult {
        return try {
            val userDocRef = firestore.collection("users").document(userId)
            // Get current date and format it for startDate
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val formattedCurrentDate = currentDate.format(formatter)

            val habitMapToAdd = mapOf(
                "habitName" to habitInfo.habitName,
                "selectedIcon" to habitInfo.selectedIcon,
                "selectedColor" to habitInfo.selectedColor.toArgb(),
                "goalCount" to habitInfo.goalCount,
                "unit" to habitInfo.unit,
                "frequency" to habitInfo.frequency,
                "selectedDays" to habitInfo.selectedDays,
                "reminders" to habitInfo.reminders.map {
                    mapOf(
                        "title" to it.title,
                        "time" to it.time,
                        "frequency" to it.frequency,
                        "selectedDays" to it.selectedDays,
                        "isEnabled" to it.isEnabled
                    )
                },
                "note" to habitInfo.note,
                "startDate" to formattedCurrentDate,
                "endDate" to habitInfo.endDate
            )

            val snapshot = userDocRef.get().await()
            val currentHabits = snapshot.get("selectedHabitsFullInfo") as? List<Map<String, Any>> ?: emptyList()
            val habitExists = currentHabits.any {
                (it["habitName"] as? String).equals(habitInfo.habitName, ignoreCase = true)
            }

            if (habitExists) {
                AddHabitResult.AlreadyExists
            } else {
                userDocRef.update("selectedHabitsFullInfo", FieldValue.arrayUnion(habitMapToAdd)).await()
                Log.d("HomeViewModel", "Successfully added popular habit: ${habitInfo.habitName}")
                fetchHabits()
                AddHabitResult.Success
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error adding popular habit: ${e.message}", e)
            AddHabitResult.Failed
        }
    }
}

enum class AddHabitResult {
    Success,
    AlreadyExists,
    Failed
}

