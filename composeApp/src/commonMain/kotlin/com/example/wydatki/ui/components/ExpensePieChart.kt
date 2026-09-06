package com.example.wydatki.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.models.Category
import com.example.wydatki.ui.formatMoney

@Composable
fun ExpensePieChart(grouped: List<Pair<Category, Double>>, total: Double) {
    val colors = listOf(
        Color(0xFF278B68), Color(0xFFC62828), Color(0xFF156082), Color(0xFFF9A825),
        Color(0xFF6A1B9A), Color(0xFFAD1457), Color(0xFF2E7D32), Color(0xFFEF6C00)
    )

    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f
            grouped.forEachIndexed { index, pair ->
                val sweepAngle = if (total > 0) (pair.second / total * 360f).toFloat() else 0f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 30.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Wydatki", fontSize = 12.sp, color = Color.Gray)
            Text(formatMoney(total), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
