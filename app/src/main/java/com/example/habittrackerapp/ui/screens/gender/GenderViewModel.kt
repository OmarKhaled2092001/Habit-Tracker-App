package com.example.habittrackerapp.ui.screens.gender

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GenderViewModel : ViewModel() {
    var selectedGender by mutableStateOf<Gender?>(null)
        private set

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun selectGender(gender: Gender) {
        selectedGender = gender
    }

    fun saveGender(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        currentUser?.let { user ->
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.update("gender", selectedGender?.name)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("GenderViewModel", "Error saving gender", e)
                    onFailure(e)
                }
        }
    }
}
