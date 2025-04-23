package com.example.habittrackerapp.navigation

import HabitsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.habittrackerapp.ui.screens.auth.check_your_email.CheckYourEmailScreen
import com.example.habittrackerapp.ui.screens.auth.register.RegisterScreen
import com.example.habittrackerapp.ui.screens.auth.forgot_password.ForgotPasswordScreen
import com.example.habittrackerapp.ui.screens.auth.login.LoginScreen
import com.example.habittrackerapp.ui.screens.gender.GenderScreen
import com.example.habittrackerapp.ui.screens.home.HomeScreen
import com.example.habittrackerapp.ui.screens.onboarding.OnboardingScreen
import com.example.habittrackerapp.ui.screens.splash.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittrackerapp.viewmodel.SharedViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun NavGraph(navController: NavHostController,viewModel: SharedViewModel) {
    // ✅ SharedViewModel initialized once and shared between screens
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(route = Screen.CheckYourEmail.route) {
            CheckYourEmailScreen(navController)
        }
        composable(route = Screen.Gender.route) {
            GenderScreen(navController)
        }
        // ✅ Pass sharedViewModel to HabitsScreen
        composable(route = Screen.Habits.route) {
            HabitsScreen(navController, sharedViewModel)
        }
        // ✅ Pass sharedViewModel to HomeScreen
        composable(route = Screen.Home.route) {
            HomeScreen(navController, sharedViewModel)
        }
    }
}
