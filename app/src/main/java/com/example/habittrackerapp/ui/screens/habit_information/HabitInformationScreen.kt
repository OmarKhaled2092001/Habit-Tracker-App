package com.example.habittrackerapp.ui.screens.habit_information

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.ui.screens.habit_information.components.CalendarSection
import com.example.habittrackerapp.ui.screens.habit_information.components.CircularProgressSection
import com.example.habittrackerapp.ui.screens.habit_information.components.HabitInformationTopAppBar
import com.example.habittrackerapp.ui.screens.habit_information.components.HabitNoteSection
import com.example.habittrackerapp.ui.screens.habit_information.components.HabitPerformanceChartSection
import com.example.habittrackerapp.ui.screens.habit_information.components.StatisticsSection
// import java.time.YearMonth // Not needed if ViewModel provides it directly

@Composable
fun HabitInformationScreen(
    navController: NavController,
    habitNameArg: String, // Argument passed from navigation
    habitInformationViewModel: HabitInformationViewModel = viewModel() // Instance of our ViewModel
) {
    // Fetch habit details when the screen is first composed or habitNameArg changes
    LaunchedEffect(habitNameArg) {
        if (habitNameArg.isNotBlank()) { // Ensure we have a name before fetching
            habitInformationViewModel.fetchHabitDetails(habitNameArg)
        }
    }

    // Collect states from the ViewModel
    val habitInfo by habitInformationViewModel.habitInfo.collectAsState()
    val isLoadingData by habitInformationViewModel.isLoading.collectAsState()

    val habitType by habitInformationViewModel.habitType.collectAsState()
    val selectedDate by habitInformationViewModel.selectedDate.collectAsState()

    // Calendar specific states
    val currentDisplayMonth by habitInformationViewModel.currentDisplayMonth.collectAsState()
    val calendarData by habitInformationViewModel.calendarData.collectAsState()
    val vmHabitColors by habitInformationViewModel.habitColors.collectAsState()

    // Counter states
    val currentProgressCount by habitInformationViewModel.currentProgressCount.collectAsState()
    val targetGoalCount by habitInformationViewModel.targetGoalCount.collectAsState()

    // Timer states
    val timerState by habitInformationViewModel.timerState.collectAsState()
    val currentTimeElapsedMillis by habitInformationViewModel.currentTimeElapsedMillis.collectAsState()
    val targetDurationMillis by habitInformationViewModel.targetDurationMillis.collectAsState()

    // Statistics states
    val doneStat by habitInformationViewModel.doneStat.collectAsState()
    val overdoneStat by habitInformationViewModel.overdoneStat.collectAsState()
    val missedStat by habitInformationViewModel.missedStat.collectAsState()
    val streakStat by habitInformationViewModel.streakStat.collectAsState()

    // Determine colors based on fetched habitInfo or use defaults
    // Use colors from ViewModel if available, otherwise generate default
    val habitColors = vmHabitColors ?: remember(habitInfo?.selectedColor) {
        generateHabitInformationColors(habitInfo?.selectedColor ?: Color(0xFFEDEDF3))
    }

    val habitNote = habitInfo?.note ?: ""
    // val currentMonth = YearMonth.from(selectedDate) // Now using currentDisplayMonth from ViewModel


    Scaffold(
        topBar = {
            HabitInformationTopAppBar(
                habitName = habitInfo?.habitName ?: (if (isLoadingData) "Loading..." else "Habit Details"),
                habitDetails = if (isLoadingData && habitInfo == null) {
                    "Loading details..."
                } else {
                    habitInfo?.let {
                        val frequencyText = if (it.frequency.equals("Daily", ignoreCase = true) || it.selectedDays.isEmpty()) {
                            "Every day"
                        } else {
                            "Every ${it.selectedDays.joinToString()}"
                        }
                        "${it.goalCount} ${it.unit} • $frequencyText"
                    } ?: "Details not available"
                },
                colors = habitColors,
                onNavigateUp = { navController.navigateUp() },
                onEdit = { /* TODO: Handle Edit action */ }
            )
        },
        containerColor = habitColors.primary
    ) { paddingValues ->
        if (isLoadingData && habitInfo == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(habitColors.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading habit details...", color = Color.White)
            }
        } else if (habitInfo == null && !isLoadingData) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(habitColors.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Could not load habit details.", color = Color.White)
                Text("Please try again or go back.", color = Color.White)
            }
        } else if (habitInfo != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                CircularProgressSection(
                    habitType = habitType,
                    habitName = habitInfo?.habitName ?: "Habit",
                    currentProgressCount = currentProgressCount,
                    targetGoalCount = targetGoalCount,
                    onIncrementCounter = { habitInformationViewModel.incrementProgressCount() },
                    onDecrementCounter = { habitInformationViewModel.decrementProgressCount() },
                    timerState = timerState,
                    currentTimeElapsedMillis = currentTimeElapsedMillis,
                    targetDurationMillis = targetDurationMillis,
                    onStartTimer = { habitInformationViewModel.startTimer() },
                    onPauseTimer = { habitInformationViewModel.pauseTimer() },
                    onStopTimer = { habitInformationViewModel.stopTimer() },
                    onResetTimer = { habitInformationViewModel.resetTimer() },
                    selectedDate = selectedDate,
                    colors = habitColors,
                    onPreviousDay = { habitInformationViewModel.updateSelectedDate(selectedDate.minusDays(1)) },
                    onNextDay = { habitInformationViewModel.updateSelectedDate(selectedDate.plusDays(1)) },
                    isLoadingProgress = false, // TODO: ViewModel should expose a specific loading state for progress updates
                    progressUpdateError = null, // TODO: ViewModel should expose error state for progress updates
                    onClearProgressError = { /* TODO: ViewModel action to clear error */ }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    if (habitNote.isNotBlank()) {
                        HabitNoteSection(
                            note = habitNote,
                            colors = habitColors
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    StatisticsSection(
                        doneValue = doneStat,
                        overdoneValue = overdoneStat,
                        streakValue = streakStat, // Ensure label in StatisticsSection is "STREAK" or similar
                        missedValue = missedStat
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    CalendarSection(
                        currentMonth = currentDisplayMonth, // Use YearMonth from ViewModel
                        habitColors = habitColors, // Pass HabitInformationColors
                        markedDays = calendarData, // Populate from ViewModel
                        onPreviousMonth = { habitInformationViewModel.onPreviousMonthClicked() },
                        onNextMonth = { habitInformationViewModel.onNextMonthClicked() },
                        onDateSelected = { dayOfMonth ->
                            // Construct LocalDate from currentDisplayMonth and selected dayOfMonth
                            val newSelectedDate = currentDisplayMonth.atDay(dayOfMonth)
                            habitInformationViewModel.updateSelectedDate(newSelectedDate)
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    HabitPerformanceChartSection(
                        // TODO: Replace with actual data from ViewModel
                        completionRate = "0%", // This could also come from ViewModel if needed elsewhere
                        chartData = List(7) { 0f }, // TODO: Populate from ViewModel
                        highlightIndex = selectedDate.dayOfWeek.value -1, // Example, adjust based on week start
                        highlightValue = "0", // TODO: Populate from ViewModel
                        colors = habitColors,
                        onDetailsClick = { /* TODO */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HabitInformationScreenPreview() {
    HabitInformationScreen(
        navController = rememberNavController(),
        habitNameArg = "Preview Habit"
    )
}

