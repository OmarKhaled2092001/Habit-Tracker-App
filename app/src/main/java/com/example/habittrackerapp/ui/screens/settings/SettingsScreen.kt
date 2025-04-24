package com.example.habittrackerapp.ui.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)) // Light gray background
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                SectionTitle("GENERAL")
                SettingSwitchItem("Dark Mode", Icons.Default.DarkMode)
                SettingRowItem("Security", Icons.Default.Lock)
                SettingRowItem("Notifications", Icons.Default.Notifications)
                SettingSwitchItem("Sounds", Icons.Default.VolumeUp, true)
                SettingSwitchItem("Vacation Mode", Icons.Default.BeachAccess)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                SectionTitle("ABOUT US")
                SettingRowItem("Rate Routinier", Icons.Default.Star)
                SettingRowItem("Share with Friends", Icons.Default.Share)
                SettingRowItem("About Us", Icons.Default.Info)
                SettingRowItem("Support", Icons.Default.Help)
            }
        }
    }
}
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray,
        modifier = Modifier
            .padding(vertical = 8.dp)
    )
}

@Composable
fun SettingRowItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    icon: ImageVector,
    initialState: Boolean = false,
    onToggle: (Boolean) -> Unit = {}
) {
    var isChecked by remember { mutableStateOf(initialState) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onToggle(it)
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(NavController(LocalContext.current))
}
