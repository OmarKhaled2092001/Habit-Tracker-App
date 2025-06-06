package com.example.habittrackerapp.ui.screens.habit_information.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsSection(
    doneValue: String,
    overdoneValue: String,
    streakValue: String,
    missedValue: String
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(value = doneValue, label = "DONE", modifier = Modifier.weight(1f))
            StatisticCard(value = overdoneValue, label = "OVERDONE", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(value = streakValue, label = "BEST STREAK", modifier = Modifier.weight(1f))
            StatisticCard(value = missedValue, label = "MISSED", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatisticCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Static white
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black // Static black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun StatisticsSectionPreview() {
    StatisticsSection("150%", "1 day", "3/3", "0")
}


@Preview(showBackground = true)
@Composable
private fun StatisticCardPreview() {
    StatisticCard(value = "150%", label = "DONE")
}


//
//package com.example.habittrackerapp.ui.screens.habit_information.components
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.habittrackerapp.ui.screens.habit_information.HabitStatistics // Import the data class
//
//@Composable
//fun StatisticsSection(
//    statistics: HabitStatistics,
//    isLoading: Boolean // Added for loading state
//) {
//    if (isLoading) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp), // Approximate height of the section
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    } else {
//        Column {
//            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                StatisticCard(value = statistics.completionRateOverall, label = "COMPLETION", modifier = Modifier.weight(1f))
//                StatisticCard(value = "${statistics.daysMetGoal}", label = "DAYS MET", modifier = Modifier.weight(1f))
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                StatisticCard(value = "${statistics.currentStreak} / ${statistics.longestStreak}", label = "STREAK (CUR/MAX)", modifier = Modifier.weight(1f))
//                StatisticCard(value = "${statistics.daysMissed}", label = "DAYS MISSED", modifier = Modifier.weight(1f))
//            }
//        }
//    }
//}
//
//@Composable
//private fun StatisticCard(value: String, label: String, modifier: Modifier = Modifier) {
//    Card(
//        modifier = modifier,
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White), // Consider theming
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(vertical = 16.dp, horizontal = 12.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = value,
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp, // Adjusted for potentially longer streak text
//                color = MaterialTheme.colorScheme.onSurface // Themed color
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = label,
//                fontSize = 11.sp, // Adjusted for potentially longer label text
//                color = MaterialTheme.colorScheme.onSurfaceVariant, // Themed color
//                lineHeight = 14.sp
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
//@Composable
//private fun StatisticsSectionPreview() {
//    StatisticsSection(
//        statistics = HabitStatistics(
//            completionRateOverall = "75%",
//            currentStreak = 5,
//            longestStreak = 10,
//            daysMetGoal = 15,
//            daysMissed = 2,
//            totalActiveDays = 20
//        ),
//        isLoading = false
//    )
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
//@Composable
//private fun StatisticsSectionLoadingPreview() {
//    StatisticsSection(
//        statistics = HabitStatistics(), // Default empty stats
//        isLoading = true
//    )
//}
//
