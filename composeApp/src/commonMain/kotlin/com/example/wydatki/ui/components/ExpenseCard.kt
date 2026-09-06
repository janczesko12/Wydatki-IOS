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
import com.example.wydatki.models.Expense
import com.example.wydatki.ui.formatMoney

@Composable
fun ExpenseCard(e: Expense, onEdit: (() -> Unit)? = null, onDelete: (() -> Unit)? = null) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    e.description.ifBlank { "Wydatek" },
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    e.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (e.note.isNotBlank()) {
                    Text(e.note, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                formatMoney(e.amount),
                fontWeight = FontWeight.Bold
            )
            if (onEdit != null) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edytuj")
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Usuń", tint = Color(0xFFC62828))
                }
            }
        }
    }
}
