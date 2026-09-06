package com.example.wydatki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.AppStore
import com.example.wydatki.models.Category
import com.example.wydatki.ui.components.CategoryDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(store: AppStore, back: () -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KATEGORIE") },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "ZARZĄDZAJ KATEGORIAMI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF455A64)
                )
            }
            
            items(store.categories.sortedBy { it.sort }) { c ->
                CategoryEditCard(
                    category = c,
                    onEdit = { editing = c; dialog = true },
                    onDelete = { store.deleteCategory(c.id) }
                )
            }
        }
    }

    if (dialog) {
        CategoryDialog(
            initial = editing,
            dismiss = { dialog = false },
            save = {
                if (editing == null) store.addCategory(it) else store.updateCategory(it)
                dialog = false
            }
        )
    }
}

@Composable
fun CategoryEditCard(category: Category, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.emoji, fontSize = 23.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                category.name,
                Modifier.weight(1f),
                fontSize = 16.sp
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edytuj", tint = Color(0xFF546E7A))
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "Usuń",
                    tint = Color.Red
                )
            }
        }
    }
}
