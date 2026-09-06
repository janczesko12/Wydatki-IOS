package com.example.wydatki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
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
import kotlinx.coroutines.launch

@Composable
fun SecurityScreen(
    store: AppStore, 
    authenticator: BiometricAuthenticator,
    onUnlocked: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (store.biometricEnabled && authenticator.isAvailable()) {
            val success = authenticator.authenticate("Użyj biometrii, aby uzyskać dostęp")
            if (success) onUnlocked()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Lock,
            null,
            Modifier.size(64.dp),
            tint = Color(0xFF278B68)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Wprowadź PIN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) { index ->
                Box(
                    Modifier
                        .size(16.dp)
                        .background(
                            if (index < pinInput.length) Color(0xFF278B68) else Color(0xFFE0E0E0),
                            CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "DEL")
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            numbers.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    row.forEach { num ->
                        if (num.isEmpty()) {
                            if (store.biometricEnabled && authenticator.isAvailable()) {
                                IconButton(
                                    onClick = { 
                                        scope.launch {
                                            val success = authenticator.authenticate("Użyj biometrii")
                                            if (success) onUnlocked()
                                        }
                                    },
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        null,
                                        Modifier.size(40.dp),
                                        tint = Color(0xFF278B68)
                                    )
                                }
                            } else {
                                Spacer(Modifier.size(64.dp))
                            }
                        } else {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFF5F7F6), CircleShape)
                                    .clickable {
                                        if (num == "DEL") {
                                            if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                        } else if (pinInput.length < 4) {
                                            pinInput += num
                                            if (pinInput.length == 4) {
                                                if (pinInput == store.securityPin) onUnlocked()
                                                else pinInput = ""
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (num == "DEL") Icon(Icons.Default.Backspace, null, tint = Color(0xFF546E7A))
                                else Text(
                                    num,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
