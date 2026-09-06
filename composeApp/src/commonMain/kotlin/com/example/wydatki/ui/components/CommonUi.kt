package com.example.wydatki.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.ui.formatMoney

@Composable
fun Empty(text: String) {
    Box(
        Modifier.fillMaxWidth().padding(30.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = Color.Gray) }
}

@Composable
fun SummaryCard(modifier: Modifier, title: String, amount: Double, bg: Color, fg: Color) {
    Card(
        modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(bg)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg)
            Text(
                formatMoney(amount),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}
