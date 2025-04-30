package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, 0),
        BottomNavItem("Add", Icons.Filled.Add, 1), // FAB
        BottomNavItem("Profile", Icons.Filled.Person, 2)
    )

    Box(modifier = modifier.fillMaxWidth()) {
        BottomAppBar(
            cutoutShape = CircleShape,
            backgroundColor = Color.White,
            elevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(30.dp))
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    if (item.index == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        IconButton(
                            onClick = { onItemSelected(item.index) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selectedIndex == item.index) Color(0xFF2962FF) else Color.Gray,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onItemSelected(1) },
            backgroundColor = Color(0xFF2962FF),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(4.dp, 8.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(56.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Habit")
        }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector, val index: Int)



@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewCustomBottomBar() {
    var selectedIndex by remember { mutableStateOf(0) }
    Box(modifier = Modifier.height(100.dp)) {
        CustomBottomBar(
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}