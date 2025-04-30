package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon // Use Material Icon
import androidx.compose.material3.Text // Use Material 3 Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.R

@Composable
fun ActionButton(text: String, iconResId: Int, iconTint: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
             .width(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(iconTint.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = iconTint,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonPreview() {
    ActionButton(text = "View", iconResId = R.drawable.show, iconTint = Color.Blue) {}

}