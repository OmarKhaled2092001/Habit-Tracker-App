//package com.example.habittrackerapp.ui.screens.habit_information.components
//
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Lightbulb
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
//import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
//
//@Composable
//fun HabitNoteSection(
//    note: String,
//    colors: HabitInformationColors
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = colors.primaryVeryLight),
//        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.Lightbulb,
//                contentDescription = "Note",
//                tint = colors.noteIcon,
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = note,
//                color = Color.DarkGray,
//                fontSize = 14.sp,
//                modifier = Modifier.weight(1f)
//            )
//        }
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//private fun HabitNoteSectionPreview() {
//    HabitNoteSection(
//        note = "Reading regularly enhances vocabulary...",
//        colors = generateHabitInformationColors(Color(0xFFF44336))
//    )
//}



package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors

@Composable
fun HabitNoteSection(
    note: String,
    colors: HabitInformationColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryVeryLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "Note",
                tint = colors.noteIcon,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = note,
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HabitNoteSectionPreview() {
    HabitNoteSection(
        note = "Reading regularly enhances vocabulary...",
        colors = generateHabitInformationColors(Color(0xFFF44336))
    )
}

