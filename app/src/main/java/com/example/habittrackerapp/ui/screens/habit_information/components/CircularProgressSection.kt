package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.HabitType
import com.example.habittrackerapp.ui.screens.habit_information.TimerState
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
import java.time.LocalDate
import java.util.concurrent.TimeUnit

// Helper function to format milliseconds to MM:SS string
fun formatMillisToMmSs(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun CircularProgressSection(
    // Habit Type and Data from ViewModel
    habitType: HabitType,
    habitName: String,

    // Counter specific data & actions
    currentProgressCount: Int,
    targetGoalCount: Int,
    onIncrementCounter: () -> Unit,
    onDecrementCounter: () -> Unit,

    // Timer specific data & actions
    timerState: TimerState,
    currentTimeElapsedMillis: Long,
    targetDurationMillis: Long,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onResetTimer: () -> Unit,

    // Common data & actions
    selectedDate: LocalDate,
    colors: HabitInformationColors,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    isLoadingProgress: Boolean,
    progressUpdateError: String?,
    onClearProgressError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val progressFraction: Float
        val progressCenterText: String
        val habitNameText = habitName // Use the passed habit name

        when (habitType) {
            HabitType.COUNTER -> {
                progressFraction =
                    if (targetGoalCount > 0) currentProgressCount.toFloat() / targetGoalCount.toFloat() else 0f
                progressCenterText = "$currentProgressCount / $targetGoalCount"
            }

            HabitType.TIMER -> {
                progressFraction =
                    if (targetDurationMillis > 0) currentTimeElapsedMillis.toFloat() / targetDurationMillis.toFloat() else 0f
                val displayTimeMillis =
                    if (timerState == TimerState.IDLE && currentTimeElapsedMillis == 0L) targetDurationMillis else targetDurationMillis - currentTimeElapsedMillis
                progressCenterText = formatMillisToMmSs(displayTimeMillis)
            }

            HabitType.UNKNOWN -> {
                progressFraction = 0f
                progressCenterText = "N/A"
            }
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            CircularProgressIndicatorCustom(
                modifier = Modifier.fillMaxSize(),
                progress = progressFraction.coerceIn(0f, 1f),
                strokeWidth = 16.dp,
                backgroundColor = colors.progressTrack,
                foregroundColor = colors.onPrimary
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = habitNameText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.onPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = progressCenterText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp,
                    color = colors.onPrimary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (habitType) {
            HabitType.COUNTER -> {
                CounterControls(
                    onIncrement = onIncrementCounter,
                    onDecrement = onDecrementCounter,
                    colors = colors,
                    isLoading = isLoadingProgress,
                    currentCount = currentProgressCount,
                    targetCount = targetGoalCount
                )
            }

            HabitType.TIMER -> {
                TimerControls(
                    timerState = timerState,
                    currentTimeElapsedMillis = currentTimeElapsedMillis,
                    onStart = onStartTimer,
                    onPause = onPauseTimer,
                    onStop = onStopTimer,
                    onReset = onResetTimer,
                    colors = colors,
                    isLoading = isLoadingProgress
                )
            }

            HabitType.UNKNOWN -> {
                Text("Habit type not recognized.", color = colors.onPrimary)
            }
        }

        if (progressUpdateError != null) {
            Text(
                text = progressUpdateError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(if (progressUpdateError != null) 8.dp else 24.dp))

        DateNavigationBar(
            selectedDate = selectedDate,
            colors = colors,
            onPreviousDay = onPreviousDay,
            onNextDay = onNextDay
        )
    }
}

@Composable
private fun CounterControls(
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    colors: HabitInformationColors,
    isLoading: Boolean,
    currentCount: Int,
    targetCount: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onDecrement,
            shape = CircleShape,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp),
            enabled = !isLoading && currentCount > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.smallButtonBackground,
                contentColor = colors.smallButtonContent,
                disabledContainerColor = colors.smallButtonBackground.copy(alpha = 0.5f),
                disabledContentColor = colors.smallButtonContent.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                Icons.Filled.Remove,
                "Decrement",
                tint = colors.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(32.dp))
        Button(
            onClick = onIncrement, // Corrected: Was onDecrement
            shape = CircleShape,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp),
            enabled = !isLoading, // Corrected: Allow incrementing beyond target
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.smallButtonBackground,
                contentColor = colors.smallButtonContent,
                disabledContainerColor = colors.smallButtonBackground.copy(alpha = 0.5f),
                disabledContentColor = colors.smallButtonContent.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                Icons.Filled.Add,
                "Increment",
                tint = colors.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun TimerControls(
    timerState: TimerState,
    currentTimeElapsedMillis: Long,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    colors: HabitInformationColors,
    isLoading: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (timerState) {
                TimerState.IDLE, TimerState.FINISHED -> {
                    Button(
                        onClick = onStart,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.onPrimary)
                    ) {
                        Icon(Icons.Filled.PlayArrow, "Start Timer", tint = colors.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start", color = colors.primary, fontSize = 16.sp)
                    }
                }

                TimerState.RUNNING -> {
                    Button(
                        onClick = onPause,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.onPrimary)
                    ) {
                        Icon(Icons.Filled.Pause, "Pause Timer", tint = colors.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pause", color = colors.primary, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onStop,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.onPrimary,
                            contentColor = colors.primary
                        )
                    ) {
                        Icon(Icons.Filled.Stop, "Stop Timer", tint = colors.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finish", color = colors.primary, fontSize = 16.sp)
                    }
                }

                TimerState.PAUSED -> {
                    Button(
                        onClick = onStart,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.onPrimary)
                    ) {
                        Icon(Icons.Filled.PlayArrow, "Resume Timer", tint = colors.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resume", color = colors.primary, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onStop,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.onPrimary,
                            contentColor = colors.primary
                        )
                    ) {
                        Icon(Icons.Filled.Stop, "Stop Timer", tint = colors.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finish", color = colors.primary, fontSize = 16.sp)
                    }
                }
            }
        }

        if (timerState == TimerState.PAUSED || timerState == TimerState.FINISHED || (timerState == TimerState.IDLE && currentTimeElapsedMillis > 0L)) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onReset,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.onPrimary,
                    contentColor = colors.primary
                )
            ) {
                Icon(Icons.Filled.Replay, "Reset Timer", tint = colors.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset", color = colors.primary, fontSize = 14.sp)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF03A9F4)
@Composable
private fun CircularProgressSectionCounterPreview() {
    val previewColors = generateHabitInformationColors(Color(0xFF03A9F4))
    CircularProgressSection(
        habitType = HabitType.COUNTER,
        habitName = "Prayers",
        currentProgressCount = 3,
        targetGoalCount = 5,
        onIncrementCounter = {},
        onDecrementCounter = {},
        timerState = TimerState.IDLE,
        currentTimeElapsedMillis = 0L,
        targetDurationMillis = 0L,
        onStartTimer = {},
        onPauseTimer = {},
        onStopTimer = {},
        onResetTimer = {},
        selectedDate = LocalDate.now(),
        colors = previewColors,
        onPreviousDay = {},
        onNextDay = {},
        isLoadingProgress = false,
        progressUpdateError = null,
        onClearProgressError = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF03A9F4)
@Composable
private fun CircularProgressSectionTimerIdlePreview() {
    val previewColors = generateHabitInformationColors(Color(0xFF03A9F4))
    CircularProgressSection(
        habitType = HabitType.TIMER,
        habitName = "Reading",
        currentProgressCount = 0,
        targetGoalCount = 0,
        onIncrementCounter = {},
        onDecrementCounter = {},
        timerState = TimerState.IDLE,
        currentTimeElapsedMillis = 0L,
        targetDurationMillis = 30 * 60 * 1000L, // 30 minutes
        onStartTimer = {},
        onPauseTimer = {},
        onStopTimer = {},
        onResetTimer = {},
        selectedDate = LocalDate.now(),
        colors = previewColors,
        onPreviousDay = {},
        onNextDay = {},
        isLoadingProgress = false,
        progressUpdateError = null,
        onClearProgressError = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF03A9F4)
@Composable
private fun CircularProgressSectionTimerRunningPreview() {
    val previewColors = generateHabitInformationColors(Color(0xFF03A9F4))
    CircularProgressSection(
        habitType = HabitType.TIMER,
        habitName = "Meditation",
        currentProgressCount = 0,
        targetGoalCount = 0,
        onIncrementCounter = {},
        onDecrementCounter = {},
        timerState = TimerState.RUNNING,
        currentTimeElapsedMillis = 10 * 60 * 1000L, // 10 minutes elapsed
        targetDurationMillis = 20 * 60 * 1000L, // 20 minutes target
        onStartTimer = {},
        onPauseTimer = {},
        onStopTimer = {},
        onResetTimer = {},
        selectedDate = LocalDate.now(),
        colors = previewColors,
        onPreviousDay = {},
        onNextDay = {},
        isLoadingProgress = false,
        progressUpdateError = null,
        onClearProgressError = {}
    )
}


