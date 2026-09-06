package com.example.wydatki.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomItem("🏠", "Pulpit", selectedTab == "dashboard") { onTabSelected("dashboard") }
        BottomItem("💸", "Wydatki", selectedTab == "expenses") { onTabSelected("expenses") }
        BottomItem("💰", "Dochody", selectedTab == "income") { onTabSelected("income") }
        BottomItem("⚙️", "Ustawienia", selectedTab == "settings") { onTabSelected("settings") }
    }
}

@Composable
fun BottomItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 20.dp,
        animationSpec = tween(180),
        label = "bottom_icon_size"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF278B68) else Color.Gray,
        animationSpec = tween(180),
        label = "bottom_text_color"
    )
    
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(icon, fontSize = iconSize.value.sp)
        Text(
            label,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
        )
    }
}
