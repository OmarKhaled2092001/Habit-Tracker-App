package com.example.habittrackerapp.ui.screens.custom_habit.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makeappssimple.abhimanyu.composeemojipicker.ComposeEmojiPickerBottomSheetUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconSelectionBottomSheet(
    sheetState: SheetState,
    selectedIcon: String, // Current icon from ViewModel (for initial display)
    selectedColor: Color, // Current color from ViewModel (for preview background)
    searchText: String, // Search text from ViewModel
    onSearchTextChange: (String) -> Unit, // Callback to ViewModel
    onIconSelected: (String) -> Unit, // Callback to ViewModel with final selection
    onDismiss: () -> Unit // This will call hideIconBottomSheet in the ViewModel
) {
    // Local state to hold the icon temporarily selected *within* the bottom sheet
    var tempSelectedIcon by remember { mutableStateOf(selectedIcon) }
    val context = LocalContext.current // Keep context for Toast

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFEDEDF3),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        windowInsets = WindowInsets(0), // Adjust if needed for system bars
        dragHandle = null, // Using custom header
        modifier = Modifier.height(650.dp) // Consider dynamic height
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BottomSheetHeader(
                title = "Habit Icon",
                subtitle = "SELECT ICON",
                onCancel = onDismiss, // Use the provided dismiss callback
                onSave = {
                    // Pass the temporarily selected icon back via the callback
                    onIconSelected(tempSelectedIcon)
                    // Dismissal is handled by the caller after saving/selection
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preview box showing the temporarily selected icon
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(selectedColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tempSelectedIcon,
                        fontSize = 56.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Emoji Picker UI
                ComposeEmojiPickerBottomSheetUI(
                    backgroundColor = Color(0xFFEDEDF3),
                    onEmojiClick = { emoji ->
                        // Update the temporary selection when an emoji is clicked
                        tempSelectedIcon = emoji.character
                    },
                    onEmojiLongClick = { emoji ->
                        // Keep Toast for simple UI feedback, or replace with Snackbar triggered via ViewModel event
                        Toast.makeText(
                            context,
                            emoji.unicodeName.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            },
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    searchText = searchText, // Use search text from ViewModel
                    updateSearchText = onSearchTextChange, // Pass changes to ViewModel
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Allow picker to take available space
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun IconSelectionBottomSheetPreview() {
    IconSelectionBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        selectedIcon = "💧",
        selectedColor = Color.Blue,
        searchText = "",
        onSearchTextChange = { },
        onIconSelected = { },
        onDismiss = { }
    )
}

