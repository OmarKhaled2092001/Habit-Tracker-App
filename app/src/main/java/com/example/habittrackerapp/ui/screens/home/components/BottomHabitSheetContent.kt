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
import androidx.compose.foundation.lazy.itemsIndexed
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

/**
 * Bottom sheet content for adding new habits.
 */
@Composable
fun BottomHabitSheetContent(
    viewModel: HomeViewModel,
    onHabitSelected: (String) -> Unit,
    onCloseSheet: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val popularHabits = listOf(
        "💧" to "Drink water",
        "🏃‍♂️" to "Run",
        "📖" to "Read books",
        "🧘‍♀️" to "Meditate",
        "👨‍💻" to "Study",
        "📕" to "Journal",
        "🌿" to "Nature",
        "😴" to "Sleep",
        "🎨" to "Paint",
        "🚶" to "Daily Steps",
        "🎮" to "Games",
        "📽️" to "Movies",
        "🏋️" to "Workout"
    )
    val colors = listOf(
        Color(0xCDEE719B),
        Color(0xC86DD572),
        Color(0x86682873),
        Color(0x9C7082E7),
        Color(0xFFBCC75A),
        Color(0xFFDE866A),
        Color(0x9E009688),
        Color(0xFF7E9652),
        Color(0xFF6C3483),
        Color(0xFF2ECC71),
        Color(0xFF3498DB),
        Color(0xFFF1C40F),
        Color(0xFFE74C3C),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Custom habit button
        Button(
            onClick = {
                navController.navigate(Screen.CustomHabit.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B5C9F),
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

        // Popular habits list
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(popularHabits) { index, (emoji, name) ->
                PopularHabitItem(
                    emoji = emoji,
                    name = name,
                    backgroundColor = colors[index % colors.size],
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            viewModel.viewModelScope.launch {
                                when (val result = viewModel.addHabitToFirestore(userId, name, emoji)) {
                                    AddHabitResult.Success -> {
                                        onHabitSelected(name)
                                        onCloseSheet()
                                    }
                                    AddHabitResult.AlreadyExists -> {
                                        Toast.makeText(context, "Habit already exists!", Toast.LENGTH_SHORT).show()
                                    }
                                    AddHabitResult.Failed -> {
                                        Toast.makeText(context, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B5C9F),
                contentColor = Color.White
            )

        ) {
            Text("Close")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomHabitSheetContentPreviw() {
    BottomHabitSheetContent(
        viewModel = remember { HomeViewModel() },
        onHabitSelected = {},
        onCloseSheet = {},
        navController = rememberNavController()
    )
}