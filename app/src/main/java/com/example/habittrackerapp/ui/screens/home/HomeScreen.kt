package com.example.habittrackerapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.ui.components.CustomBottomBar
import com.example.habittrackerapp.ui.screens.home.components.BottomHabitSheetContent
import com.example.habittrackerapp.ui.screens.home.components.HomeContent
import com.example.habittrackerapp.ui.screens.home.components.ProfileContent
import com.example.habittrackerapp.ui.screens.home.components.showDatePicker
import kotlinx.coroutines.launch

// Import HabitInfo if needed for preview or specific type references, though viewModel handles the type
// import com.example.habittrackerapp.data.HabitInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // Use the updated HomeViewModel which now works with HabitInfo
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedDate by viewModel.selectedDate.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    // habits state now correctly holds List<HabitInfo> from the updated ViewModel
    val habits by viewModel.habits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Fetch initial data (ViewModel handles fetching HabitInfo)
    LaunchedEffect(Unit) {
        viewModel.fetchUserFullName()
        viewModel.fetchHabits()
    }

    // State for bottom navigation and sheet (remains the same)
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDF3))
    ) {
        // Conditionally render HomeContent or ProfileContent based on selectedIndex
        when (selectedIndex) {
            0, 1 -> HomeContent(
                navController = navController,
                fullName = fullName,
                selectedDate = selectedDate,
                habits = habits, // Pass the List<HabitInfo>
                isLoading = isLoading,
                onDateClick = {
                    showDatePicker(context) { pickedDate ->
                        viewModel.setSelectedDate(pickedDate)
                    }
                },
                onNotificationClick = { /* TODO: Implement notification logic */ },
                onDateSelected = { viewModel.setSelectedDate(it) },
                // Pass the correct deleteHabit function reference (takes HabitInfo)
                onDeleteHabit = { habitInfo -> viewModel.deleteHabit(habitInfo) }
            )
            2 -> ProfileContent(navController = navController)
        }

        // Bottom navigation bar (remains the same)
        CustomBottomBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                showBottomSheet = when (index) {
                    0 -> false // Home: Hide bottom sheet
                    1 -> true  // Add Habit: Show bottom sheet (as requested)
                    2 -> false // Profile: Hide bottom sheet
                    else -> false
                }
            },
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // Bottom sheet for adding habits (remains the same, uses updated ViewModel)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedIndex = 0 // Reset to Home tab
                },
                sheetState = sheetState
            ) {
                BottomHabitSheetContent(
                    viewModel = viewModel, // Pass the updated ViewModel
                    onHabitSelected = { /* Optional: Handle habit selection */ },
                    onCloseSheet = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            selectedIndex = 0 // Reset to Home tab
                        }
                    },
                    navController = navController
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    // Preview might need a mock ViewModel providing sample HabitInfo data
    // For simplicity, using the default viewModel() might work if dependencies are simple
    // or if the preview HomeViewModel provides sample data.
    HomeScreen(navController = rememberNavController())
}

