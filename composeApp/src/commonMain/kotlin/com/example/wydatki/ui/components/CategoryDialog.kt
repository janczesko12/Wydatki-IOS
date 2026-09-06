package com.example.wydatki.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.wydatki.models.Category
import com.example.wydatki.ui.randomUUID

@Composable
fun CategoryDialog(initial: Category?, dismiss: () -> Unit, save: (Category) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var emoji by remember { mutableStateOf(initial?.emoji ?: "💰") }
    
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(if (initial == null) "Dodaj kategorię" else "Edytuj kategorię") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton({
                if (name.isNotBlank()) {
                    save(
                        Category(
                            id = initial?.id ?: randomUUID(),
                            name = name.trim(),
                            emoji = emoji.ifBlank { "💰" },
                            sort = initial?.sort ?: 999
                        )
                    )
                }
            }) { Text("ZAPISZ") }
        },
        dismissButton = { TextButton(dismiss) { Text("ANULUJ") } }
    )
}
