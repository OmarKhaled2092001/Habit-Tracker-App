package com.example.habittrackerapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Register : Screen("register")
    object CheckYourEmail : Screen("check_your_email")
    object Gender : Screen("gender")
    object NewHabit : Screen("new_habit")
    object Home : Screen("home")
    object CustomHabit : Screen("custom_habit")


    object HabitInformation : Screen("habit_information/{habitName}") {
        fun createRoute(habitName: String) = "habit_information/$habitName"
    }

    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")

}
