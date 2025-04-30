package com.example.habittrackerapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.R

@Composable
fun AppBar(
    onDateClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDateClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(15.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = "Select date",
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp)
            )
        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(15.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.notification),
                contentDescription = "Notifications",
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    AppBar(onDateClick = {}, onNotificationClick = {})

}
