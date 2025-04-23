package com.example.habittrackerapp.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding


import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon

import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.FractionalThreshold
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

import androidx.compose.runtime.remember

import androidx.compose.ui.res.painterResource
import com.example.habittrackerapp.R











@Composable
fun ReusableCard(
    emoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color(0xFF2F56F9) else Color.White
    val contentColor = if (selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 16.sp, color = contentColor)
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5CFF))
    ) {
        Text(text = text, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SwipeableHabitCard(
    habitName: String,
    progressText: String,
    emoji: String,
    onDone: () -> Unit,
    onView: () -> Unit,
    onFail: @Composable () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { 160.dp.toPx() }
    val anchors = mapOf(
        -sizePx to -1,
        0f to 0,
        sizePx to 1
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        if (swipeableState.currentValue == -1) {
            Row(
                Modifier
                    .matchParentSize()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton("Fail", R.drawable.close, Color.Red, onClick = {})
                Spacer(Modifier.width(8.dp))
                ActionButton("Skip", R.drawable.arrow_right, Color.Gray, onClick = onSkip)
            }
        } else if (swipeableState.currentValue == 1) {
            Row(
                Modifier
                    .matchParentSize()
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton("View", R.drawable.show, Color(0xFF4CA1FF), onClick = onView)
                Spacer(Modifier.width(8.dp))
                ActionButton("Done", R.drawable.tick_square, Color(0xFF4CAF50), onClick = onDone)
            }
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxSize(),
            elevation = 6.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color(0xFFB3E5FC).copy(alpha = 0.6f), // Light blue with transparency
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 26.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = habitName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(text = progressText, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Composable
fun ActionButton(text: String, iconResId: Int, iconTint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        Icons.Filled.Home,
        Icons.Default.Explore,
        Icons.Default.Add,
        Icons.Default.EmojiEvents,
        Icons.Default.Person
    )

    Box(modifier = modifier) { // ✅ Now using the passed-in modifier
        BottomAppBar(
            cutoutShape = CircleShape,
            backgroundColor = Color.White,
            elevation = 10.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(
                    color = Color(0xFFCCCDD0),
                    width = 1.dp,
                    shape = RoundedCornerShape(30.dp)
                )
                .fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, icon ->
                    when (index) {
                        2 -> Spacer(modifier = Modifier.width(50.dp))
                        else -> IconButton(onClick = { onItemSelected(index) }) {
                            Box {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (selectedIndex == index) Color(0xFF2962FF) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onItemSelected(2) },
            backgroundColor = Color(0xFF2962FF),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 8.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSwipeableHabitCard() {
    SwipeableHabitCard(
        habitName = "Drink Water",
        progressText = "2 of 5 times today",
        emoji = "💧",
        onDone = {},
        onView = {},
        onFail = {},
        onSkip = {}
    )
}