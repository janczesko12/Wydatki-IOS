package com.example.wydatki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wydatki.AppStore
import com.example.wydatki.ui.components.Empty
import com.example.wydatki.ui.components.ExpenseCard
import com.example.wydatki.ui.components.ExpenseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(store: AppStore, month: String, back: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var dialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<com.example.wydatki.models.Expense?>(null) }
    
    val list = store.expenses.filter {
        monthOf(it.date) == month && (query.isBlank() || it.description.contains(
            query,
            true
        ) || it.note.contains(query, true))
    }.sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WYDATKI • ${getMonthLabel(month)}") },
                navigationIcon = { IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = null; dialog = true },
                containerColor = Color(0xFF278B68)
            ) { Icon(Icons.Default.Add, "Dodaj", tint = Color.White) }
        }
    ) { p ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(p)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Szukaj") },
                    singleLine = true
                )
            }
            items(list) { e ->
                ExpenseCard(e,
                    onEdit = { editing = e; dialog = true },
                    onDelete = { store.deleteExpense(e.id) }
                )
            }
            if (list.isEmpty()) {
                item { Empty("Brak wydatków.") }
            }
        }
    }

    if (dialog) {
        val category = store.categories.firstOrNull { it.id == editing?.categoryId }
            ?: store.categories.firstOrNull()

        if (category != null) {
            ExpenseDialog(
                c = category,
                month = month,
                initial = editing,
                dismiss = { dialog = false },
                save = {
                    if (editing == null) store.addExpense(it) else store.updateExpense(it)
                    dialog = false
                }
            )
        }
    }
}
