package com.example.habittrackerapp.ui.screens.home.components

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun fetchFullName(onFullNameFetched: (String?) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    currentUser?.uid?.let { uid ->
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val fullName = "$firstName $lastName".trim()
                    onFullNameFetched(fullName)
                } else {
                    onFullNameFetched(null)
                }
            }
            .addOnFailureListener {
                onFullNameFetched(null)
            }
    } ?: onFullNameFetched(null)
}


