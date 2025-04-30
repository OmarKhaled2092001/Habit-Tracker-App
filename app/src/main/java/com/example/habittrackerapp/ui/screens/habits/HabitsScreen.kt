package com.example.habittrackerapp.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habittrackerapp.data.HabitItem
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.ReusableCard
import com.example.habittrackerapp.ui.components.primary_button.PrimaryButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun HabitsScreen(
    navController: NavHostController,
    viewModel: HabitsViewModel = viewModel()
) {
    val habits = listOf(
        HabitItem("💧", "Drink water"),
        HabitItem("🏃‍♂️", "Run"),
        HabitItem("📖", "Read books"),
        HabitItem("🧘‍♀️", "Meditate"),
        HabitItem("👨‍💻", "Study"),
        HabitItem("📕", "Journal"),
        HabitItem("🌿", "Nature"),
        HabitItem("😴", "Sleep"),
        HabitItem("🎨", "Paint"),
        HabitItem("🚶", "Daily Steps"),
        HabitItem("🎮", "Games"),
        HabitItem("📽️", "Movies"),
        HabitItem("🏋️", "Workout"),
    )

    val selectedHabits by viewModel.selectedHabits.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(Color(0xFFF8F9FB))
    ) {
        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFf6f9ff))
                ) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Choose your first habits",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "You may add more habits later",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(habits) { habit ->
                ReusableCard(
                    emoji = habit.emoji,
                    label = habit.name,
                    selected = selectedHabits.contains(habit),
                    onClick = {
                        if (selectedHabits.contains(habit)) {
                            viewModel.removeHabitFromSelection(habit)
                        } else {
                            viewModel.addHabitToSelection(habit)
                        }
                    }
                )
            }
        }

        PrimaryButton(
            text = "Next",
            onClick = {
                viewModel.saveSelectedHabits(
                    onSuccess = {
                        navController.navigate(Screen.HomeScreen.route)
                    },
                    onFailure = { exception ->
                        // Handle error if you want
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun HabitsScreenPreview() {
    HabitsScreen(navController = rememberNavController())
}
