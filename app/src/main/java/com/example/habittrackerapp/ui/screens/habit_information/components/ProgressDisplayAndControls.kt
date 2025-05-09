//package com.example.habittrackerapp.ui.screens.habit_information.components
//
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.PauseCircleOutline
//import androidx.compose.material.icons.filled.PlayCircleOutline
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.material3.CircularProgressIndicator // For loading state
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
//import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
//
//@Composable
//fun ProgressDisplayAndControls(
//    progress: Int,
//    maxProgress: Int,
//    timerDurationText: String?,
//    isTimerRunning: Boolean,
//    colors: HabitInformationColors,
//    onAddProgress: () -> Unit,
//    onRemoveProgress: () -> Unit,
//    onTimerClick: () -> Unit,
//    isLoading: Boolean // Added to reflect loading state
//) {
//    val progressFraction = if (maxProgress > 0) progress.toFloat() / maxProgress.toFloat() else 0f
//
//    val timerIcon = if (isTimerRunning) Icons.Filled.PauseCircleOutline else Icons.Filled.PlayCircleOutline
//    val animatedTimerIconColor by animateColorAsState(
//        targetValue = if (isTimerRunning) colors.primaryDark else colors.onPrimaryMedium,
//        label = "TimerIconColorAnimation"
//    )
//
//    Box(contentAlignment = Alignment.Center) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(0.85f)
//                .align(Alignment.Center),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            SmallIconButton(
//                icon = Icons.Default.Remove,
//                onClick = onRemoveProgress,
//                colors = colors,
//                enabled = !isLoading // Disable if loading
//            )
//
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                if (timerDurationText != null) {
//                    SmallIconButton(
//                        icon = timerIcon,
//                        onClick = onTimerClick,
//                        colors = colors, // Use base colors
//                        iconTint = animatedTimerIconColor, // Apply animated tint
//                        modifier = Modifier.padding(bottom = 8.dp),
//                        enabled = !isLoading // Disable if loading
//                    )
//                }
//                SmallIconButton(
//                    icon = Icons.Default.Add,
//                    onClick = onAddProgress,
//                    colors = colors,
//                    enabled = !isLoading // Disable if loading
//                )
//            }
//        }
//
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier.size(150.dp)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(60.dp), // Smaller indicator over the main one
//                    color = colors.onPrimary, // Or a contrasting color
//                    strokeWidth = 4.dp
//                )
//            } else {
//                CircularProgressIndicatorCustom(
//                    progress = progressFraction,
//                    strokeWidth = 12.dp,
//                    backgroundColor = colors.progressTrack,
//                    foregroundColor = colors.onPrimary,
//                    modifier = Modifier.fillMaxSize()
//                )
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(
//                        text = progress.toString(),
//                        color = colors.onPrimary,
//                        fontSize = 40.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = if (timerDurationText != null) timerDurationText else "/${maxProgress}",
//                        color = colors.onPrimaryMedium,
//                        fontSize = 16.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//private fun ProgressDisplayAndControlsPreview() {
//    val previewColors = generateHabitInformationColors(Color(0xFF00796B)) // Teal color for preview
//    ProgressDisplayAndControls(
//        progress = 3,
//        maxProgress = 7,
//        timerDurationText = "15 min",
//        isTimerRunning = false,
//        colors = previewColors,
//        onAddProgress = {},
//        onRemoveProgress = {},
//        onTimerClick = {},
//        isLoading = false
//    )
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//private fun ProgressDisplayAndControlsLoadingPreview() {
//    val previewColors = generateHabitInformationColors(Color(0xFF00796B))
//    ProgressDisplayAndControls(
//        progress = 3,
//        maxProgress = 7,
//        timerDurationText = "15 min",
//        isTimerRunning = true,
//        colors = previewColors,
//        onAddProgress = {},
//        onRemoveProgress = {},
//        onTimerClick = {},
//        isLoading = true
//    )
//}
