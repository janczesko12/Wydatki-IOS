package com.example.wydatki.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinDialog(dismiss: () -> Unit, onSet: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(1) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(
                if (step == 1) "Ustaw kod PIN" else "Potwierdź kod PIN",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (step == 1) "Wprowadź 4 cyfry" else "Wprowadź PIN ponownie",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val current = if (step == 1) pin else confirm
                    repeat(4) { index ->
                        Box(
                            Modifier
                                .size(16.dp)
                                .background(
                                    if (index < current.length) Color(0xFF278B68) else Color(0xFFE0E0E0),
                                    CircleShape
                                )
                        )
                    }
                }
                if (error.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(Modifier.height(24.dp))
                val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "DEL")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    numbers.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            row.forEach { num ->
                                if (num.isEmpty()) Spacer(Modifier.size(54.dp))
                                else Box(
                                    Modifier
                                        .size(54.dp)
                                        .background(Color(0xFFF5F7F6), CircleShape)
                                        .clickable {
                                            if (num == "DEL") {
                                                if (step == 1 && pin.isNotEmpty()) pin = pin.dropLast(1)
                                                if (step == 2 && confirm.isNotEmpty()) confirm =
                                                    confirm.dropLast(1)
                                            } else {
                                                if (step == 1 && pin.length < 4) {
                                                    pin += num
                                                    if (pin.length == 4) {
                                                        step = 2
                                                        error = ""
                                                    }
                                                } else if (step == 2 && confirm.length < 4) {
                                                    confirm += num
                                                    if (confirm.length == 4) {
                                                        if (confirm == pin) onSet(pin)
                                                        else {
                                                            confirm = ""
                                                            error = "Kody PIN nie są zgodne."
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (num == "DEL") Icon(
                                        Icons.Default.Backspace,
                                        null,
                                        Modifier.size(22.dp),
                                        tint = Color(0xFF546E7A)
                                    )
                                    else Text(
                                        num,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF263238)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(dismiss) { Text("ANULUJ") } }
    )
}
