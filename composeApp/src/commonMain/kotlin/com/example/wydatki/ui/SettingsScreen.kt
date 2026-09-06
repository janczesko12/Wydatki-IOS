package com.example.wydatki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.AppStore
import com.example.wydatki.ui.components.PinDialog

@Composable
fun SettingsScreen(
    store: AppStore,
    filePicker: FilePicker,
    onManageCategories: () -> Unit,
    back: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                "USTAWIENIA",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            "☁️ Synchronizacja z chmurą",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(store.cloudStatus, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        
                        if (store.cloudStatus.contains("połączona")) {
                            Text(
                                "Konto jest zalogowane. Dane są synchronizowane z Firestore.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { store.logout() }) { Text("WYLOGUJ") }
                        } else {
                            Text(
                                "Aby odzyskać dane po usunięciu aplikacji, zaloguj się do konta Firebase.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("E-mail") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Hasło") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    store.login(email, password) { _, text ->
                                        message = text
                                    }
                                }) { Text("ZALOGUJ") }
                                Button(onClick = {
                                    store.register(email, password) { _, text -> 
                                        message = text 
                                    }
                                }) { Text("UTWÓRZ KONTO") }
                            }
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Kopia zapasowa", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Dodatkowa kopia wszystkich danych w pliku JSON.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { 
                                filePicker.exportJson(store.exportJson(), "Wydatki-backup.json") { _, text ->
                                    message = text
                                }
                            }) { Text("EKSPORTUJ") }
                            
                            Button(onClick = {
                                filePicker.importJson { json ->
                                    if (json != null) {
                                        if (store.importJson(json)) message = "Kopia została przywrócona."
                                        else message = "Nieprawidłowy plik kopii."
                                    } else {
                                        message = "Nie udało się odczytać kopii."
                                    }
                                }
                            }) { Text("IMPORTUJ") }
                        }
                        if (message.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(message, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth().clickable(onClick = onManageCategories), colors = CardDefaults.cardColors(Color.White)) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏷️", fontSize = 20.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Kategorie", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Zarządzaj listą kategorii wydatków.", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, Modifier.size(16.dp), tint = Color.Gray)
                    }
                }
            }

            item {
                var pinDialog by remember { mutableStateOf(false) }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Bezpieczeństwo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Zabezpiecz dostęp do aplikacji kodem PIN lub biometrią.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFF278B68), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Kod PIN", Modifier.weight(1f))
                            Switch(
                                checked = store.securityPin != null,
                                onCheckedChange = {
                                    if (it) pinDialog = true
                                    else {
                                        store.setPin(null)
                                        store.setBiometric(false)
                                    }
                                }
                            )
                        }

                        if (store.securityPin != null) {
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Fingerprint, null, tint = Color(0xFF278B68), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Text("Logowanie biometryczne", Modifier.weight(1f))
                                Switch(
                                    checked = store.biometricEnabled,
                                    onCheckedChange = { store.setBiometric(it) }
                                )
                            }
                        }
                    }
                }
                if (pinDialog) {
                    PinDialog(
                        dismiss = { pinDialog = false },
                        onSet = { store.setPin(it); pinDialog = false }
                    )
                }
            }
            
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("O aplikacji", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Wydatki v1.1 (KMP)", fontSize = 13.sp)
                        Text("© 2026 Projekt Wydatki-iOS", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
