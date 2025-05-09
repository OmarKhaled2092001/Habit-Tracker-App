//package com.example.habittrackerapp.ui.screens.habit_information.components
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.Icon // Changed from material3.Icon to material.Icon to match original usage
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.Button // material3.Button is fine
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.habittrackerapp.ui.screens.habit_information.HabitInformationColors
//import com.example.habittrackerapp.ui.screens.habit_information.generateHabitInformationColors
//
//@Composable
//fun SmallIconButton(
//    modifier: Modifier = Modifier,
//    icon: ImageVector? = null,
//    text: String? = null,
//    onClick: () -> Unit,
//    colors: HabitInformationColors, // This is the custom color object
//    enabled: Boolean = true, // Added enabled parameter
//    iconTint: Color? = null    // Added iconTint parameter, defaults to colors.smallButtonContent if null
//) {
//    Button(
//        onClick = onClick,
//        shape = CircleShape,
//        modifier = modifier.size(40.dp),
//        contentPadding = PaddingValues(0.dp),
//        enabled = enabled, // Use the enabled parameter
//        colors = ButtonDefaults.buttonColors(
//            containerColor = colors.smallButtonBackground,
//            contentColor = colors.smallButtonContent,
//            disabledContainerColor = colors.smallButtonBackground.copy(alpha = 0.5f), // Example disabled color
//            disabledContentColor = colors.smallButtonContent.copy(alpha = 0.5f)   // Example disabled content color
//        )
//    ) {
//        if (icon != null) {
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                // Use iconTint if provided, otherwise default to the button's content color (colors.smallButtonContent)
//                tint = iconTint ?: colors.smallButtonContent,
//                modifier = Modifier.size(20.dp)
//            )
//        }
//        if (text != null) {
//            Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//        }
//    }
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFF6A7DFF)
//@Composable
//private fun SmallIconButtonPreview() {
//    val previewColors = generateHabitInformationColors(Color(0xFF6A7DFF))
//    SmallIconButton(
//        icon = Icons.Default.Add,
//        onClick = {},
//        colors = previewColors
//    )
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFF6A7DFF)
//@Composable
//private fun SmallIconButtonDisabledPreview() {
//    val previewColors = generateHabitInformationColors(Color(0xFF6A7DFF))
//    SmallIconButton(
//        icon = Icons.Default.Add,
//        onClick = {},
//        colors = previewColors,
//        enabled = false
//    )
//}
//
//@Preview(showBackground = true, backgroundColor = 0xFF6A7DFF)
//@Composable
//private fun SmallIconButtonWithTintPreview() {
//    val previewColors = generateHabitInformationColors(Color(0xFF6A7DFF))
//    SmallIconButton(
//        icon = Icons.Default.Add,
//        onClick = {},
//        colors = previewColors,
//        iconTint = Color.Red
//    )
//}