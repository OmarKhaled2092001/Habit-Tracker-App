package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.R
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitInformationTopAppBar(
    habitName: String,
    habitDetails: String,
    colors: HabitInformationColors,
    onNavigateUp: () -> Unit,
    onEdit: () -> Unit
) {
    TopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(habitName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    habitDetails,
                    fontSize = 12.sp,
                    color = colors.onPrimaryMedium
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onEdit) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.primary,
            titleContentColor = colors.onPrimary,
            navigationIconContentColor = colors.onPrimary,
            actionIconContentColor = colors.onPrimary
        )
    )
}


@Preview(showBackground = true, backgroundColor = 0xFF4CAF50)
@Composable
private fun HabitInformationTopAppBarPreview() {
    val previewColors = generateHabitInformationColors(Color(0xFF4CAF50))
    HabitInformationTopAppBar(
        habitName = "Morning Run",
        habitDetails = "5 km • Daily",
        colors = previewColors,
        onNavigateUp = {},
        onEdit = {}
    )
}