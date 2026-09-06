package com.example.wydatki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wydatki.AppStore
import com.example.wydatki.models.Income
import com.example.wydatki.ui.components.Empty
import com.example.wydatki.ui.components.IncomeCard
import com.example.wydatki.ui.components.IncomeDialog
import com.example.wydatki.ui.components.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(store: AppStore, month: String, back: () -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Income?>(null) }
    
    val list = store.incomes.filter { monthOf(it.date) == month }
        .sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DOCHODY • ${getMonthLabel(month)}") },
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
                SummaryCard(
                    Modifier.fillMaxWidth(),
                    "SUMA DOCHODÓW",
                    list.sumOf { it.amount },
                    Color(0xFFE2F4E9),
                    Color(0xFF087443)
                )
            }
            items(list) { i ->
                IncomeCard(i,
                    onEdit = { editing = i; dialog = true },
                    onDelete = { store.deleteIncome(i.id) }
                )
            }
            if (list.isEmpty()) {
                item { Empty("Brak dochodów.") }
            }
        }
    }

    if (dialog) {
        IncomeDialog(
            initial = editing,
            month = month,
            dismiss = { dialog = false },
            save = {
                if (editing == null) store.addIncome(it) else store.updateIncome(it)
                dialog = false
            }
        )
    }
}
