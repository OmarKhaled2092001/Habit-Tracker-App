import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.navigation.Screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.data.HabitItem
import com.example.habittrackerapp.ui.PrimaryButton
import com.example.habittrackerapp.viewmodel.SharedViewModel
import com.example.habittrackerapp.ui.ReusableCard
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HabitsScreen(navController: NavHostController, viewModel: SharedViewModel) {
    val habits = listOf(
        HabitItem("💧","Drink water" ),
        HabitItem("🏃‍♂️","Run"),
        HabitItem("📖","Read books"),
        HabitItem("🧘‍♀️","Meditate" ),
        HabitItem("👨‍💻","Study"),
        HabitItem("📕","Journal"),
        HabitItem("🌿","Nature"),
        HabitItem("😴","Sleep")
    )

    val selectedHabits = viewModel.selectedHabits // Already a StateList in ViewModel

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
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    selectedHabits.forEach { habit ->
                        viewModel.addHabitToFirestore(
                            userId = userId,
                            name = habit.name,
                            emoji = habit.emoji,
                            onSuccess = {},
                            onFailure = {}
                        )
                    }
                }
                navController.navigate(Screen.Home.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitsScreen() {
    val navController = rememberNavController()
    HabitsScreen(navController = navController, viewModel = SharedViewModel())
}
