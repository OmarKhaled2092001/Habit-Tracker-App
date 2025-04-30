package com.example.habittrackerapp.ui.screens.auth.login

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.utils.isValidEmail
import com.google.firebase.auth.*
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider

class LoginViewModel : ViewModel() {

    var loginState by mutableStateOf(LoginFormState())
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChanged(value: String) {
        val isValid = value.isValidEmail()
        loginState = loginState.copy(email = value, emailValid = isValid)
    }

    fun onPasswordChanged(value: String) {
        loginState = loginState.copy(password = value)
    }

//    fun canLogin(): Boolean {
//        return loginState.emailValid && loginState.password.length >= 6
//    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val email = loginState.email.trim()
        val password = loginState.password

        if (email.isBlank() || password.isBlank()) {
            onError("Email and password must not be empty")
            return
        }

        loginState = loginState.copy(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginState = loginState.copy(isLoading = false)
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        onError("Please verify your email address first")
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> onError("No account found with this email")
                        is FirebaseAuthInvalidCredentialsException -> onError("Invalid credentials")
                        else -> onError(task.exception?.message ?: "Login failed")
                    }
                }
            }
    }

    fun handleFacebookAccessToken(
        token: AccessToken,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        loginState = loginState.copy(isLoading = true)

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                loginState = loginState.copy(isLoading = false)
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Facebook login failed")
                }
            }
    }
}
