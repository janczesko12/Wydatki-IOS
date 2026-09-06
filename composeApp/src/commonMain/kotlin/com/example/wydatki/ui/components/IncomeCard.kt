package com.example.wydatki.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.models.Income
import com.example.wydatki.ui.formatMoney

@Composable
fun IncomeCard(i: Income, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    i.source,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    i.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (i.note.isNotBlank()) {
                    Text(i.note, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                formatMoney(i.amount),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edytuj")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Usuń", tint = Color.Red)
            }
        }
    }
}
