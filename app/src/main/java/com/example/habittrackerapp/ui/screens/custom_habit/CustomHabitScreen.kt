package com.example.habittrackerapp.ui.screens.custom_habit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.habittrackerapp.navigation.Screen
import com.example.habittrackerapp.ui.screens.custom_habit.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomHabitScreen(
    navController: NavController,
    viewModel: CustomHabitScreenViewModel = viewModel()
) {
    val habitState by viewModel.habitState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showIconBottomSheet by remember { mutableStateOf(false) }
    var showColorBottomSheet by remember { mutableStateOf(false) }
    var showGoalBottomSheet by remember { mutableStateOf(false) }
    var showReminderBottomSheet by remember { mutableStateOf(false) }

    var editingReminderId by remember { mutableStateOf<String?>(null) }

    val iconSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val goalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    LaunchedEffect(habitState.saveSuccess) {
        if (habitState.saveSuccess) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            viewModel.resetSaveStatus()
        }
    }

    LaunchedEffect(habitState.saveError) {
        if (habitState.saveError != null) {
            Toast.makeText(context, "Error: ${habitState.saveError}", Toast.LENGTH_LONG).show()
            viewModel.resetSaveStatus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEDF3))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HabitHeader(
                onBackClick = { navController.popBackStack() },
                onDoneClick = {
                    if (!habitState.isSaving) {
                        viewModel.saveCustomHabitToFirestore()
                    }
                },
                isDoneEnabled = !habitState.isSaving
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitNameInput(
                habitName = habitState.habitName,
                onNameChange = viewModel::updateHabitName,
                maxChars = 40
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconAndColorSelection(
                selectedIcon = habitState.selectedIcon,
                selectedColor = habitState.selectedColor,
                onIconClick = { scope.launch { iconSheetState.show() }; showIconBottomSheet = true },
                onColorClick = { scope.launch { colorSheetState.show() }; showColorBottomSheet = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitGoal(
                goalCount = habitState.goalCount,
                unit = habitState.unit,
                frequency = habitState.frequency,
                selectedDays = habitState.selectedDays,
                onChangeClick = { scope.launch { goalSheetState.show() }; showGoalBottomSheet = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitReminders(
                reminders = habitState.reminders,
                onAddReminderClick = {
                    editingReminderId = null
                    scope.launch { reminderSheetState.show() }
                    showReminderBottomSheet = true
                },
                onToggleReminder = { reminderId, isEnabled ->
                    viewModel.updateReminder(reminderId = reminderId, isEnabled = isEnabled)
                },
                onDeleteReminder = viewModel::deleteReminder,
                onEditReminder = { reminder ->
                    editingReminderId = reminder.id
                    scope.launch { reminderSheetState.show() }
                    showReminderBottomSheet = true
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitNote(
                note = habitState.note,
                onNoteChanged = viewModel::updateNote
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabitPeriod(
                startDate = habitState.displayStartDate,
                endDate = habitState.displayEndDate,
                onStartDateSelected = viewModel::updateStartDate,
                onEndDateSelected = viewModel::updateEndDate
            )
            Spacer(modifier = Modifier.height(32.dp))
        }


        if (habitState.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }




    if (showIconBottomSheet) {
        IconSelectionBottomSheet(
            sheetState = iconSheetState,
            selectedIcon = habitState.selectedIcon,
            selectedColor = habitState.selectedColor,
            searchText = habitState.iconSearchText,
            onSearchTextChange = viewModel::updateIconSearchText,
            onIconSelected = {
                viewModel.updateSelectedIcon(it)
                scope.launch { iconSheetState.hide() }
                showIconBottomSheet = false
            },
            onDismiss = { showIconBottomSheet = false; viewModel.updateIconSearchText("") }
        )
    }


    if (showColorBottomSheet) {
        ColorSelectionBottomSheet(
            sheetState = colorSheetState,
            selectedIcon = habitState.selectedIcon,
            selectedColor = habitState.selectedColor,
            onColorSelected = {
                viewModel.updateSelectedColor(it)
                scope.launch { colorSheetState.hide() }
                showColorBottomSheet = false
            },
            onDismiss = { showColorBottomSheet = false }
        )
    }


    if (showGoalBottomSheet) {
        GoalSelectionBottomSheet(
            sheetState = goalSheetState,
            initialGoalCount = habitState.goalCount,
            initialUnit = habitState.unit,
            initialFrequency = habitState.frequency,
            initialSelectedDays = habitState.selectedDays,
            onSave = { goalCount, unit, frequency, days ->
                viewModel.updateGoal(goalCount, unit, frequency, days)
                scope.launch { goalSheetState.hide() }
                showGoalBottomSheet = false
            },
            onDismiss = { showGoalBottomSheet = false }
        )
    }

    // Reminder Bottom Sheet
    if (showReminderBottomSheet) {
        val initialReminder = editingReminderId?.let { id ->
            habitState.reminders.find { it.id == id }
        }
        ReminderBottomSheet(
            sheetState = reminderSheetState,
            initialReminder = initialReminder,
            onSave = { title, time, frequency, days ->
                if (editingReminderId == null) {
                    viewModel.addReminder(title, time, frequency, days)
                } else {
                    viewModel.updateReminder(
                        reminderId = editingReminderId!!,
                        title = title,
                        time = time,
                        frequency = frequency,
                        selectedDays = days
                    )
                }
                scope.launch { reminderSheetState.hide() }
                showReminderBottomSheet = false
                editingReminderId = null
            },
            onDismiss = {
                scope.launch { reminderSheetState.hide() }
                showReminderBottomSheet = false
                editingReminderId = null
            }
        )
    }
}



@Preview(showBackground = true)
@Composable
private fun CustomHabitScreenPreview() {
    CustomHabitScreen(navController = rememberNavController())
}

