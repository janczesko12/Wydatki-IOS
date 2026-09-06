package com.example.wydatki

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.wydatki.ui.*
import com.example.wydatki.ui.components.BottomBar

@Composable
fun App(
    store: AppStore,
    biometricAuthenticator: BiometricAuthenticator,
    filePicker: FilePicker
) {
    var isLocked by remember { mutableStateOf(store.securityPin != null) }

    if (isLocked) {
        SecurityScreen(
            store = store,
            authenticator = biometricAuthenticator,
            onUnlocked = { isLocked = false }
        )
        return
    }

    var screen by remember { mutableStateOf("dashboard") }
    var month by remember { mutableStateOf(getCurrentMonth()) }
    
    val selectedTab = when (screen) {
        "expenses" -> "expenses"
        "income" -> "income"
        "settings" -> "settings"
        else -> "dashboard"
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF6F8F7)
        ) {
            Column {
                Box(Modifier.weight(1f)) {
                    Crossfade(
                        targetState = screen,
                        animationSpec = tween(200),
                        label = "screen_transition"
                    ) { currentScreen ->
                        when (currentScreen) {
                            "dashboard" -> DashboardScreen(
                                store = store,
                                month = month,
                                setMonth = { month = it },
                                onCategoryClick = { /* placeholder */ },
                                onTabClick = { screen = it }
                            )
                            "expenses" -> ExpensesScreen(
                                store = store,
                                month = month,
                                back = { screen = "dashboard" }
                            )
                            "income" -> IncomeScreen(
                                store = store,
                                month = month,
                                back = { screen = "dashboard" }
                            )
                            "category" -> CategoryScreen(
                                store = store,
                                back = { screen = "settings" }
                            )
                            "stats" -> StatisticsScreen(
                                store = store,
                                month = month,
                                setMonth = { month = it },
                                back = { screen = "dashboard" }
                            )
                            "search" -> SearchScreen(
                                store = store,
                                back = { screen = "dashboard" }
                            )
                            "settings" -> SettingsScreen(
                                store = store,
                                filePicker = filePicker,
                                onManageCategories = { screen = "category" },
                                back = { screen = "dashboard" }
                            )
                        }
                    }
                }
                
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { screen = it }
                )
            }
        }
    }
}
