package com.example.habittrackerapp.ui.screens.home.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed // Keep using itemsIndexed for color cycling
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.R
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.screens.home.AddHabitResult
import com.example.habittrackerapp.ui.screens.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
// Import necessary components and data
import com.example.habittrackerapp.data.habits as popularHabits // Use data from HabitData.kt, alias for clarity
import com.example.habittrackerapp.ui.components.habit_card.HabitCard // Import HabitCard
import com.example.habittrackerapp.utils.HabitColors // Import HabitColors for cycling
import com.example.habittrackerapp.data.HabitInfo // Import HabitInfo for data model

/**
 * Bottom sheet content for adding new habits.
 */
@Composable
fun BottomHabitSheetContent(
    viewModel: HomeViewModel,
    onHabitSelected: (String) -> Unit, // Keep this callback if needed after adding
    onCloseSheet: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    // Data is now sourced from HabitData.habits (aliased as popularHabits)

    // Get the color options for cycling
    val colors = HabitColors.colorOptions

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Custom habit button (remains unchanged)
        Button(
            onClick = {
                navController.navigate(Screen.CustomHabit.route)
                onCloseSheet() // Close sheet when navigating to custom habit screen
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2962FF),
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create custom habit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Popular Habits",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Popular habits list - Updated to use LazyRow with HabitCard
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Use itemsIndexed to get index for color cycling, consistent with NewHabitScreen approach
            itemsIndexed(popularHabits) { index, habit -> // habit is HabitInfo
                HabitCard(
                    habitName = habit.habitName,
                    selectedIcon = habit.selectedIcon,
                    selectedColor = habit.selectedColor, // Use color from HabitInfo data
                    goalCount = habit.goalCount,
                    unit = habit.unit,
                    isSelected = true, // Force color display, not actual selection state
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            viewModel.viewModelScope.launch {
                                // *** CORRECTED: Call the updated ViewModel function ***
                                when (val result = viewModel.addPopularHabitToFirestore(userId, habit)) { // Pass the whole HabitInfo object
                                    AddHabitResult.Success -> {
                                        // Pass the added habit's name back
                                        onHabitSelected(habit.habitName)
                                        onCloseSheet() // Close sheet on success
                                    }
                                    AddHabitResult.AlreadyExists -> {
                                        Toast.makeText(context, "Habit '${habit.habitName}' already exists!", Toast.LENGTH_SHORT).show()
                                    }
                                    AddHabitResult.Failed -> {
                                        Toast.makeText(context, "Failed to add habit. Try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            // Handle case where user is not logged in (optional, but good practice)
                            Toast.makeText(context, "Please log in to add habits.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Close button (remains unchanged)
        Button(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2962FF),
                contentColor = Color.White
            )
        ) {
            Text("Close")
        }
    }
}

// Preview needs adjustment as it now relies on HabitData and HabitColors
@Preview(showBackground = true)
@Composable
private fun BottomHabitSheetContentPreview() {
    // Note: Preview might need a mock ViewModel or adjustments
    // if HomeViewModel has complex dependencies.
    // For simplicity, using remember { HomeViewModel() } might work if dependencies are simple.
    BottomHabitSheetContent(
        viewModel = remember { HomeViewModel() }, // Assuming basic ViewModel instantiation works for preview
        onHabitSelected = { /* Preview action */ },
        onCloseSheet = { /* Preview action */ },
        navController = rememberNavController() // Use rememberNavController for preview
    )
}
