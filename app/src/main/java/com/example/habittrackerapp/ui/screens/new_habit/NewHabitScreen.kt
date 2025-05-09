package com.example.habittrackerapp.ui.screens.new_habit

import android.widget.Toast // For showing errors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box // For potential loading overlay
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize // For potential loading overlay
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator // For loading indicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment // For potential loading overlay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // For Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.screens.new_habit.components.CustomHabitButton
import com.example.habittrackerapp.ui.components.habit_card.HabitCard
import com.example.habittrackerapp.ui.screens.new_habit.components.HabitHeader

@Composable
fun NewHabitScreen(
    navController: NavController,
    viewModel: NewHabitScreenViewModel = viewModel() // Inject ViewModel
) {
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle save success/failure side effects
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.navigate(Screen.Home.route) {
                // Optional: Clear back stack up to a certain point if needed
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            viewModel.resetSaveStatus() // Reset status after handling
        }
    }

    LaunchedEffect(uiState.saveError) {
        if (uiState.saveError != null) {
            Toast.makeText(context, "Error: ${uiState.saveError}", Toast.LENGTH_LONG).show()
            viewModel.resetSaveStatus() // Reset status after handling
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEDEDF3))
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HabitHeader(
                        onBackClick = { navController.popBackStack() },
                        // Trigger save function in ViewModel on Done click
                        onDoneClick = {
                            if (!uiState.isSaving) { // Prevent multiple clicks
                                viewModel.saveSelectedHabitsToFirestore()
                            }
                        },
                        // Optionally disable Done button while saving
//                         isDoneEnabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomHabitButton(
                        // Consider moving navigation logic to ViewModel/Coordinator
                        onClick = { navController.navigate(Screen.CustomHabit.route) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular Habits",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            // Habit Cards - Use data from ViewModel state
            itemsIndexed(uiState.popularHabits) { index, habit ->
                HabitCard(
                    habitName = habit.habitName,
                    selectedIcon = habit.selectedIcon,
                    // Get color from ViewModel logic
                    selectedColor = viewModel.getHabitColor(index),
                    goalCount = habit.goalCount,
                    unit = habit.unit,
                    // Check selection state from ViewModel
                    isSelected = uiState.selectedHabits.contains(habit),
                    // Trigger action in ViewModel on click
                    onClick = { viewModel.toggleHabitSelection(habit) }
                )
            }
        }

        // Show loading indicator if saving
        if (uiState.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)), // Semi-transparent overlay
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// Note: HabitHeader might need an optional `isDoneEnabled: Boolean = true` parameter
// to visually disable the button during saving.

@Preview(showBackground = true)
@Composable
private fun NewHabitScreenPreview() {
    // Preview might need adjustment depending on ViewModel setup or use a fake ViewModel
    NewHabitScreen(navController = rememberNavController())
}

