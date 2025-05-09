package com.example.habittrackerapp.ui.screens.habit_information

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Immutable
data class HabitInformationColors(
    val primary: Color,
    val primaryLight: Color,
    val primaryVeryLight: Color,
    val primaryDark: Color,
    val onPrimary: Color,
    val onPrimaryMedium: Color,
    val progressTrack: Color,
    val smallButtonBackground: Color,
    val smallButtonContent: Color,
    val noteIcon: Color
)

fun generateHabitInformationColors(primary: Color): HabitInformationColors {
    val primaryLight = primary.copy(alpha = 0.7f)
    val primaryVeryLight = primary.copy(alpha = 0.1f)
    val onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White

    return HabitInformationColors(
        primary = primary,
        primaryLight = primaryLight,
        primaryVeryLight = primaryVeryLight,
        primaryDark = primary.copy(alpha = 0.8f),
        onPrimary = onPrimary,
        onPrimaryMedium = onPrimary.copy(alpha = 0.8f),
        progressTrack = Color.White.copy(alpha = 0.3f),
        smallButtonBackground = Color.White,
        smallButtonContent = primary,
        noteIcon = primary.copy(alpha = 0.8f)
    )
}