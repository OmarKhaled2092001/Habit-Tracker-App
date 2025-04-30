package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class) // Needed for swipeable
@Composable
fun SwipeableHabitCard(
    habitName: String,
    emoji: String,
    progressText: String,
    onDone: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit, // Added onClick parameter
    modifier: Modifier = Modifier
) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { 90.dp.toPx() }
    val anchors = mapOf(
        -sizePx to -1,
        0f to 0,
        sizePx to 1
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .background(Color.Transparent)
    ) {
        // Left swipe (Delete)
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton("Delete", R.drawable.close, Color(0xFFFF7043), onClick = onDelete)
        }

        // Right swipe (Done)
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton("Done", R.drawable.tick_square, Color(0xFF66BB6A), onClick = onDone)
        }

        // The Habit Card
        Card(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onClick() }, // Added clickable modifier
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.Transparent
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FB))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFE3F2FD), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = habitName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = progressText, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FB)
@Composable
fun PreviewSwipeableHabitCard() {
    SwipeableHabitCard(
        habitName = "Drink Water",
        progressText = "2 of 5 times today",
        emoji = "💧",
        onDone = { println("Done clicked") },
        onDelete = { println("Delete clicked") },
        onClick = { println("Card clicked") } // Added onClick for preview
    )
}