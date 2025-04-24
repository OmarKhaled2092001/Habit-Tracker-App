package com.example.habittrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.navigation.NavGraph
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.facebook.CallbackManager
import com.facebook.appevents.AppEventsLogger
import androidx.activity.viewModels
import com.example.habittrackerapp.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppEventsLogger.activateApp(application)

        callbackManager = CallbackManager.Factory.create()

        enableEdgeToEdge()
        setContent {
            HabitTrackerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        callbackManager = callbackManager,
                        viewModel = sharedViewModel)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
