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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.data.HabitItem
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.components.SwipeableHabitCard

/**
 * Main content of the HomeScreen, including app bar, greeting, day picker, and habits list.
 */
@Composable
fun HomeContent(
    navController: NavController,
    fullName: String?,
    selectedDate: java.time.LocalDate,
    habits: List<HabitItem>,
    isLoading: Boolean,
    onDateClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onDateSelected: (java.time.LocalDate) -> Unit,
    onDeleteHabit: (HabitItem) -> Unit
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
            HabitsList(habits = habits, onDeleteHabit = onDeleteHabit, navControler = navController)
        }
    }
}

/**
 * LazyColumn displaying the list of habits with swipeable cards.
 */
@Composable
private fun HabitsList(
    habits: List<HabitItem>,
    onDeleteHabit: (HabitItem) -> Unit,
    navControler: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(habits) { habit ->
            SwipeableHabitCard(
                habitName = habit.name,
                emoji = habit.emoji,
                progressText = "0 of 5 today",
                onDone = { /* TODO: Implement done logic */ },
                onDelete = { onDeleteHabit(habit) },
                onClick = { navControler.navigate(Screen.HabitInformation.route) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        fullName = "John Doe",
        selectedDate = java.time.LocalDate.now(),
        habits = emptyList(),
        isLoading = false,
        onDateClick = {},
        onNotificationClick = {},
        onDateSelected = {},
        onDeleteHabit = {},
        navController = rememberNavController()
    )
}