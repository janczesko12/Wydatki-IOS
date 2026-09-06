package com.example.wydatki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.AppStore
import com.example.wydatki.ui.components.Empty
import com.example.wydatki.ui.components.ExpenseCard

@Composable
fun SearchScreen(store: AppStore, back: () -> Unit) {
    var query by remember { mutableStateOf("") }
    
    val list = store.expenses.filter {
        query.isNotBlank() && (it.description.contains(
            query,
            true
        ) || it.note.contains(
            query,
            true
        ) || store.categories.firstOrNull { c -> c.id == it.categoryId }?.name?.contains(
            query,
            true
        ) == true)
    }.sortedByDescending { it.date }

    Column(Modifier.fillMaxSize().background(Color(0xFFF6F8F7))) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = back) { 
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć") 
            }
            Text(
                "WYSZUKIWANIE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Szukaj w opisach, notatkach i kategoriach") },
                    singleLine = true
                )
            }
            if (query.isNotBlank()) {
                item {
                    Text(
                        "Znaleziono: ${list.size}",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            items(list) { e -> 
                ExpenseCard(e) 
            }
            if (query.isNotBlank() && list.isEmpty()) {
                item { Empty("Brak wyników.") }
            }
        }
    }
}
