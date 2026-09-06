package com.example.wydatki.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.models.Category
import com.example.wydatki.models.Expense
import com.example.wydatki.ui.formatDate
import com.example.wydatki.ui.randomUUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDialog(
    c: Category,
    month: String,
    initial: Expense?,
    dismiss: () -> Unit,
    save: (Expense) -> Unit
) {
    var amount by remember {
        mutableStateOf(
            initial?.amount?.toString()?.replace('.', ',') ?: ""
        )
    }
    var desc by remember { mutableStateOf(initial?.description ?: "") }
    var date by remember {
        mutableStateOf(initial?.date ?: "$month-01")
    }
    var note by remember { mutableStateOf(initial?.note ?: "") }
    var error by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(if (initial == null) "Dodaj wydatek" else "Edytuj wydatek") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    amount,
                    { amount = it },
                    label = { Text("Kwota") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    desc,
                    { desc = it },
                    label = { Text("Opis") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data") },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    trailingIcon = {
                        IconButton({ showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, null)
                        }
                    },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.LightGray,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color(0xFF278B68)
                    )
                )
                OutlinedTextField(
                    note,
                    { note = it },
                    label = { Text("Notatka") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotBlank()) Text(error, color = Color.Red, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton({
                val n = amount.replace(',', '.').toDoubleOrNull()
                if (n == null || n <= 0) {
                    error = "Podaj poprawną kwotę."
                } else if (desc.isBlank()) {
                    error = "Podaj opis."
                } else {
                    save(
                        Expense(
                            id = initial?.id ?: randomUUID(),
                            categoryId = c.id,
                            amount = n,
                            description = desc.trim(),
                            date = date.trim(),
                            note = note.trim()
                        )
                    )
                }
            }) { Text("ZAPISZ") }
        },
        dismissButton = { TextButton(dismiss) { Text("ANULUJ") } }
    )

    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton({
                    state.selectedDateMillis?.let {
                        date = formatDate(it)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state)
        }
    }
}
