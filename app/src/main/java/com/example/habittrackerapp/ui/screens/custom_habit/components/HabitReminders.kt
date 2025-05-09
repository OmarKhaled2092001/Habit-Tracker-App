package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.R
import com.example.habittrackerapp.ui.components.action_button.ActionButton
import com.example.habittrackerapp.ui.screens.custom_habit.Reminder
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun HabitReminders(
    reminders: List<Reminder>, // Accept list from ViewModel
    onAddReminderClick: () -> Unit, // Callback to show bottom sheet
    onToggleReminder: (reminderId: String, isEnabled: Boolean) -> Unit, // Callback to ViewModel
    onDeleteReminder: (reminderId: String) -> Unit, // Callback to ViewModel
    onEditReminder: (reminder: Reminder) -> Unit // Callback to ViewModel/Screen for edit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        RemindersHeader()
        Spacer(modifier = Modifier.height(8.dp))
        // Use the callback to trigger the bottom sheet
        AddReminderCard(onClick = onAddReminderClick)
        Spacer(modifier = Modifier.height(8.dp))
        RemindersList(
            reminders = reminders,
            onEdit = onEditReminder, // Pass down the edit callback
            onDelete = onDeleteReminder, // Pass down the delete callback
            onToggle = onToggleReminder // Pass down the toggle callback
        )
    }
}

@Composable
private fun RemindersHeader() {
    Text(
        text = "REMINDERS",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
}

@Composable
private fun AddReminderCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add reminder",
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ADD A REMINDER",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}


@Composable
private fun RemindersList(
    reminders: List<Reminder>,
    onEdit: (Reminder) -> Unit,
    onDelete: (reminderId: String) -> Unit,
    onToggle: (reminderId: String, Boolean) -> Unit
) {
    reminders.forEach { reminder ->
        ReminderCard(
            reminder = reminder,
            // Pass reminder ID to toggle and delete callbacks
            onToggle = { isEnabled -> onToggle(reminder.id, isEnabled) },
            onDelete = { onDelete(reminder.id) },
            onEdit = { onEdit(reminder) } // Pass the whole reminder object for editing
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderCard(
    reminder: Reminder,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { 120.dp.toPx() } // Width of delete/edit buttons
    val anchors = mapOf(
        0f to 0, // Swiped state
        -sizePx to 1 // Revealed state
    )
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        // Background actions (Edit, Delete)
        Row(
            modifier = Modifier
                .matchParentSize() // Fill the height and width of the Box
                .background(Color.Transparent) // Or a suitable background color
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                label = "Edit",
                iconRes = R.drawable.edit,
                backgroundColor = Color(0xFFFFCA28), // Yellow
                onClick = {
                    scope.launch { swipeableState.animateTo(0) } // Close swipe before action
                    onEdit()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionButton(
                label = "Delete",
                iconRes = R.drawable.delete,
                backgroundColor = Color(0xFFFF7043), // Red
                onClick = {
                    scope.launch { swipeableState.animateTo(0) } // Close swipe before action
                    onDelete()
                }
            )
        }

        // Foreground Card content
        Card(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = reminder.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = reminder.isEnabled,
                        onCheckedChange = onToggle,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .semantics { contentDescription = "Toggle reminder ${reminder.title}" },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF2962FF),
                            checkedTrackColor = Color(0xFFE3F2FD),
                            uncheckedThumbColor = Color.Gray,
//                             uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD) // Light blue background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.alarm),
                                contentDescription = "Reminder time",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = reminder.time,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar_weekly),
                                contentDescription = "Reminder schedule",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                // Use the computed schedule property from Reminder data class
                                text = reminder.schedule,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun HabitRemindersPreview() {
    val sampleReminders = remember {
        mutableStateListOf(
            Reminder(title = "Morning Walk", time = "7:00 AM", frequency = "Daily", selectedDays = emptyList(), isEnabled = true),
            Reminder(title = "Read Book", time = "9:00 PM", frequency = "Weekly", selectedDays = listOf("Mon", "Wed", "Fri"), isEnabled = false)
        )
    }
    HabitReminders(
        reminders = sampleReminders,
        onAddReminderClick = {},
        onToggleReminder = { id, isEnabled ->
            val index = sampleReminders.indexOfFirst { it.id == id }
            if (index != -1) sampleReminders[index] = sampleReminders[index].copy(isEnabled = isEnabled)
        },
        onDeleteReminder = { id -> sampleReminders.removeIf { it.id == id } },
        onEditReminder = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ReminderCardPreview() {
    ReminderCard(
        reminder = Reminder(
            title = "Morning Workout",
            time = "8:00 AM",
            frequency = "Weekly",
            selectedDays = listOf("Mon", "Wed"),
            isEnabled = true
        ),
        onToggle = {},
        onDelete = {},
        onEdit = {}
    )
}

