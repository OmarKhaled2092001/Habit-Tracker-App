package com.example.habittrackerapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.data.HabitItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SharedViewModel : ViewModel() {
    var username by mutableStateOf("User") // default for preview

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Fetch user details (username) after login
    fun fetchUserDetails() {
        val user = auth.currentUser
        user?.let {
            username = it.displayName ?: "User"  // Set the username to the logged-in user's display name (if available)
        }
    }
    // Store selected habits (UI logic)
    private val _selectedHabits = mutableStateListOf<HabitItem>()
    val selectedHabits: List<HabitItem> get() = _selectedHabits

    // Store all habits from Firestore
    val allHabits = mutableStateListOf<HabitItem>()


    fun addHabitToSelection(habit: HabitItem) {
        if (!_selectedHabits.contains(habit)) {
            _selectedHabits.add(habit)
        }
    }

    fun removeHabitFromSelection(habit: HabitItem) {
        _selectedHabits.remove(habit)
    }


    fun fetchHabits(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .addOnSuccessListener { result ->
                allHabits.clear()
                _selectedHabits.clear() // for saving when I log in again
                for (document in result) {
                    val name = document.getString("name")
                    val emoji = document.getString("emoji")
                    val id = document.id // Add this to get the document ID
                    if (name != null && emoji != null) {
                        val habit = HabitItem(name, emoji, id) // Include id
                        allHabits.add(habit)
                        _selectedHabits.add(habit) // for saving when I log in again
                    }
                }
            }
    }

    fun deleteHabitFromFirestore(habit: HabitItem, onComplete: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = Firebase.firestore

        userId?.let {
            db.collection("users")
                .document(it)
                .collection("habits")
                .document(habit.id)
                .delete()
                .addOnSuccessListener {
                    onComplete()
                }
                .addOnFailureListener {
                    Log.e("DeleteHabit", "Failed to delete", it)
                }
        }
    }



    fun addHabitToFirestore(
        userId: String,
        name: String,
        emoji:String,
        onSuccess: () -> Unit ={},
        onFailure: (Exception) -> Unit ={}
    ) {
        val habitData = hashMapOf(
            "name" to name,
            "emoji" to emoji
        )

        db.collection("users")
            .document(userId)
            .collection("habits")
            .add(habitData)
            .addOnSuccessListener {
                allHabits.add(HabitItem(name, emoji))

                //  allHabits.add(emoji)
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
