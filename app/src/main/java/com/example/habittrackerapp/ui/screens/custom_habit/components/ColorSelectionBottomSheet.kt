package com.example.habittrackerapp.ui.screens.custom_habit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember // Keep remember for local temp state
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.utils.HabitColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSelectionBottomSheet(
    sheetState: SheetState,
    selectedIcon: String, // Current icon from ViewModel (for preview)
    selectedColor: Color, // Current color from ViewModel (for initial display)
    onColorSelected: (Color) -> Unit, // Callback to ViewModel with final selection
    onDismiss: () -> Unit
) {
    // Local state to hold the color temporarily selected *within* the bottom sheet
    var tempSelectedColor by remember { mutableStateOf(selectedColor) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFEDEDF3),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.height(650.dp) // Consider dynamic height
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BottomSheetHeader(
                title = "Habit Color",
                subtitle = "SELECT COLOR",
                onCancel = onDismiss, // Use the provided dismiss callback
                onSave = {
                    // Save the selected icon and dismiss the bottom sheet
                    onColorSelected(tempSelectedColor)
                    onDismiss() // Call onDismiss to hide the bottom sheet
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preview box showing the icon with the temporarily selected color
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(tempSelectedColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedIcon,
                        fontSize = 56.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(HabitColors.colorOptions.size) { index ->
                        val color = HabitColors.colorOptions[index]
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(color, CircleShape)
                                .clickable { tempSelectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == tempSelectedColor) {
                                Text(
                                    text = "✔",
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ColorSelectionBottomSheetPreview() {
    ColorSelectionBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        selectedIcon = "👍",
        selectedColor = Color(0xFF2196F3),
        onColorSelected = {},
        onDismiss = {}
    )
}




