package com.example.habittrackerapp.ui.screens.home

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.habittrackerapp.R
import com.example.habittrackerapp.ui.SwipeableHabitCard
import com.example.habittrackerapp.viewmodel.SharedViewModel

import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import com.example.habittrackerapp.data.HabitItem
import com.example.habittrackerapp.ui.CustomBottomBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

import androidx.compose.runtime.remember as remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val selectedHabits = viewModel.selectedHabits
    // val selectedHabitsforremove = remember { mutableStateListOf<HabitItem>() }

    // val selectedHabits by viewModel.selectedHabits.collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val weekDates = getWeekFromDate(selectedDate)
//
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            viewModel.fetchHabits(uid)
            viewModel.fetchUserDetails()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
                .padding(top = 50.dp, start = 12.dp, end = 12.dp)
        ) {
            // 🔼 Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "Calendar",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            showDatePicker(context) { pickedDate ->
                                selectedDate = pickedDate
                            }
                        }
                        .background(Color.White, RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFA5A6A9),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(8.dp)
                )

                Row {
                    Icon(
                        painterResource(R.drawable.notification),
                        contentDescription = "Notification",
                        Modifier
                            .size(50.dp)
                            .background(Color.White, RoundedCornerShape(15.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFA5A6A9),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Hi,${viewModel.username} 👋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Let's make habits together!", color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // 📅 Horizontal Day Picker (Now dynamic!)
            LazyRow {
                items(weekDates) { date ->
                    val isToday = date == LocalDate.now()
                    Column(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                color = if (isToday) Color.Blue else Color(0xFFA5A6A9),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) Color.Blue else Color.Black,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                .uppercase(),
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Challenges", fontWeight = FontWeight.Bold)
                Text("View All", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("🏃‍♂️ Best Runners! - 5 days 13 hrs left")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Habits", fontWeight = FontWeight.Bold)
                Text("View All", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (selectedHabits.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.no_data), // replace with your image
                        contentDescription = "No habits",
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Text("No habits yet!")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 80.dp)
                ) {


                    items(selectedHabits) { habititem ->
                        SwipeableHabitCard(
                            habitName = habititem.emoji,
                            progressText = "Progress Text Here",
                            emoji = habititem.name,
                            onDone = {
                                // Show a star popup and save "completed" status
                                showHabitCompletedPopup(context = context, habit = habititem)
                                saveHabitStatusToFirestore(habititem, "completed")
                            },
                            onView = {
                                // Navigate to EditHabitScreen with habit details
                                navController.navigate("edit_habit_screen/${habititem.id}")
                            },
                            onFail = {
                                // Delete from Firestore and remove from selectedHabits list
                                deleteHabitFromFirestore(habititem) {
                                    viewModel.removeHabitFromSelection(habititem)

                                }

                            },
                            onSkip = {
                                // Mark as skipped and maybe show a toast
                                saveHabitStatusToFirestore(habititem, "skipped")
                                Toast.makeText(context, "Habit skipped. You got this tomorrow!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }


                }
            }

        }


    }
    var selectedIndex by remember { mutableIntStateOf(0) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CustomBottomBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> {
                        // Home tapped
                        // You can navigate or update UI here
                    }
                    1 -> {
                        // Explore tapped
                    }
                    2 -> {
                        // FAB tapped - open bottom sheet
                        showBottomSheet = true
                    }
                    3 -> {
                        // Achievements
                    }
                    4 -> {
                        // Profile
                    }
                }
            },
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter) // Aligning at the bottom center
                .fillMaxWidth()
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            BottomHabitSheetContent(
                viewModel = viewModel,
                onHabitSelected = { habit ->
                    // Optional: Toast or logging
                },
                onCloseSheet = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                }
            )
        }
    }

}



fun getWeekFromDate(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.with(DayOfWeek.MONDAY)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

fun showDatePicker(context: Context, onDatePicked: (LocalDate) -> Unit) {
    val today = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDatePicked(pickedDate)
        },
        today.get(Calendar.YEAR),
        today.get(Calendar.MONTH),
        today.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun BottomHabitSheetContent(
    viewModel: SharedViewModel,
    onHabitSelected: (String) -> Unit,
    onCloseSheet: () -> Unit
) {
    val popularHabits = listOf(
        "💧" to "Drink water",
        "🏃‍♂️" to "Run",
        "📖" to "Read books",
        "🧘‍♀️" to "Meditate",
        "👨‍💻" to "Study",
        "📕" to "Journal",
        "🌿" to "Nature",
        "😴" to "Sleep"
    )
    val colorsH = listOf<Color>(
        Color(0xCDEE719B),
        Color(0xC86DD572),
        Color(0x86682873),
        Color(0x9C7082E7),
        Color(0xFFBCC75A),
        Color(0xFFDE866A),
        Color(0x9E009688),
        Color(0xFF7E9652),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        var value by remember { mutableStateOf("") }

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Custom Habit") },
            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF00C853),
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Popular Habits", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(popularHabits) { index,(emoji, name)  ->
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(colorsH[index % colorsH.size])
                        .clickable {
                            val userId =
                                FirebaseAuth.getInstance().currentUser?.uid ?: return@clickable
                            val habit = HabitItem(name, emoji)
                            viewModel.addHabitToSelection(habit)
                            viewModel.addHabitToFirestore(userId, name, emoji)

                            onHabitSelected(name)
                            onCloseSheet()
                        }
                        .padding(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = emoji, fontSize = 30.sp, modifier = Modifier.padding(start = 4.dp,top=4.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = name,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Close")
        }
    }
}

fun showHabitCompletedPopup(context: Context, habit: HabitItem) {
    Toast.makeText(context, "⭐ Habit '${habit.name}' completed!", Toast.LENGTH_LONG).show()
    // You could also use a real Dialog with animation here
}
fun saveHabitStatusToFirestore(habit: HabitItem, status: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    userId?.let {
        db.collection("users")
            .document(it)
            .collection("habitStatus")
            .document(habit.id)
            .set(
                mapOf(
                    "name" to habit.name,
                    "emoji" to habit.emoji,
                    "status" to status,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            )
    }
}
fun deleteHabitFromFirestore(habit: HabitItem, onComplete: () -> Unit,) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    userId?.let {
        db.collection("users")
            .document(it)
            .collection("habits")
            .document(habit.id)
            .delete()
            .addOnSuccessListener {
                onComplete()  // Call the onComplete callback after successful deletion
            }
            .addOnFailureListener { exception ->
                // Handle failure (e.g., show a Toast or Log error)
                //  Toast.makeText(context, "Failed to delete habit: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
@Preview(showBackground = true)
@Composable
fun BottomHabitSheetContentPreview() {
    // Creating a simple mock view model and functions for the preview
    val mockViewModel = remember { SharedViewModel() }

    // Sample habit selection handler
    val onHabitSelected: (String) -> Unit = { habit ->
        println("Habit selected: $habit")
    }

    // Sample close sheet handler
    val onCloseSheet: () -> Unit = {
        println("Sheet closed")
    }

    // Calling the BottomHabitSheetContent function with mock data
    BottomHabitSheetContent(
        viewModel = mockViewModel,
        onHabitSelected = onHabitSelected,
        onCloseSheet = onCloseSheet
    )
}