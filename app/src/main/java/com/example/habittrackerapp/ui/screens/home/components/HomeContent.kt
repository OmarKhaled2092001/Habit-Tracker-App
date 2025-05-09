package com.example.habittrackerapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.data.HabitInfo // Import HabitInfo
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.components.SwipeableHabitCard
import java.time.LocalDate

/**
 * Main content of the HomeScreen, including app bar, greeting, day picker, and habits list.
 */
@Composable
fun HomeContent(
    navController: NavController,
    fullName: String?,
    selectedDate: LocalDate,
    habits: List<HabitInfo>, // Updated to use HabitInfo
    isLoading: Boolean,
    onDateClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDeleteHabit: (HabitInfo) -> Unit // Updated to use HabitInfo
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        AppBar(onDateClick = onDateClick, onNotificationClick = onNotificationClick)
        Spacer(modifier = Modifier.height(16.dp))
        GreetingSection(fullName = fullName)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDayPicker(selectedDate = selectedDate, onDateSelected = onDateSelected)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "My Habits",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Pass HabitInfo list and delete function
            HabitsList(habits = habits, onDeleteHabit = onDeleteHabit, navController = navController)
        }
    }
}

/**
 * LazyColumn displaying the list of habits with swipeable cards.
 */
@Composable
private fun HabitsList(
    habits: List<HabitInfo>, // Updated to use HabitInfo
    onDeleteHabit: (HabitInfo) -> Unit, // Updated to use HabitInfo
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp), // Adjust padding to avoid overlap with bottom bar
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(habits, key = { it.habitName }) { habit -> // Use habitName as a key for stability
            // Construct description dynamically (example)
            val description = "Goal: ${habit.goalCount} ${habit.unit} ${habit.frequency}"
            // Or keep the old description if progress tracking isn't implemented yet:
            // val description = "0 of ${habit.goalCount} today"

            SwipeableHabitCard(
                habitName = habit.habitName,
                selectedIcon = habit.selectedIcon,
                selectedIconBackgroundColor = habit.selectedColor, // Use color from HabitInfo
                description = description, // Use dynamic or placeholder description
                onDone = { /* TODO: Implement done logic based on HabitInfo */ },
                onDelete = { onDeleteHabit(habit) }, // Pass the HabitInfo object
                onClick = {
                    navController.navigate(Screen.HabitInformation.createRoute(habit.habitName))
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    // Preview needs sample HabitInfo data
    val sampleHabits = listOf(
        HabitInfo(
            habitName = "Drink Water",
            selectedIcon = "💧",
            selectedColor = Color(0xFF41B3E8),
            goalCount = "8",
            unit = "glasses",
            frequency = "Daily",
            selectedDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
            reminders = emptyList(),
            note = "",
            startDate = "01/01/2024",
            endDate = ""
        )
    )
    HomeContent(
        fullName = "John Doe",
        selectedDate = LocalDate.now(),
        habits = sampleHabits, // Use sample HabitInfo
        isLoading = false,
        onDateClick = {},
        onNotificationClick = {},
        onDateSelected = {},
        onDeleteHabit = {},
        navController = rememberNavController()
    )
}
