package com.example.wydatki.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.AppStore
import com.example.wydatki.models.Category
import com.example.wydatki.ui.components.SummaryCard

@Composable
fun DashboardScreen(
    store: AppStore,
    month: String,
    setMonth: (String) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onTabClick: (String) -> Unit
) {
    val ex = store.expenses.filter { monthOf(it.date) == month }
    val inc = store.incomes.filter { monthOf(it.date) == month }
    val totalIncome = inc.sumOf { it.amount }
    val totalExpenses = ex.sumOf { it.amount }

    Scaffold(
        containerColor = Color(0xFFF6F8F7)
    ) { p ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(p)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MonthHeader(month, setMonth)
                SummarySection(totalIncome, totalExpenses, totalIncome - totalExpenses)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickButton("🔎", "Szukaj") { onTabClick("search") }
                    QuickButton("📊", "Statystyki") { onTabClick("stats") }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "WYDATKI WG KATEGORII",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF455A64)
                )
            }
            items(store.categories) { c ->
                val categoryAmount = ex.filter { it.categoryId == c.id }.sumOf { it.amount }
                CategoryCard(c, categoryAmount) { onCategoryClick(c) }
            }
        }
    }
}

@Composable
fun RowScope.QuickButton(icon: String, text: String, onClick: () -> Unit) {
    Card(
        Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon)
            Spacer(Modifier.width(6.dp))
            Text(
                text,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MonthHeader(month: String, set: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { set(shiftMonth(month, -1)) }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Poprzedni"
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                getMonthLabel(month),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Miesiąc", fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(onClick = { set(shiftMonth(month, 1)) }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                "Następny"
            )
        }
    }
}

@Composable
fun SummarySection(income: Double, expenses: Double, remaining: Double) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryCard(Modifier.weight(1f), "DOCHÓD", income, Color(0xFFE2F4E9), Color(0xFF087443))
        SummaryCard(
            Modifier.weight(1f),
            "WYDATKI",
            expenses,
            Color(0xFFFCE7E5),
            Color(0xFFC62828)
        )
    }
    Spacer(Modifier.height(8.dp))
    SummaryCard(
        Modifier.fillMaxWidth(),
        "POZOSTAŁO",
        remaining,
        Color(0xFFE5F1F8),
        Color(0xFF156082)
    )
}

@Composable
fun CategoryCard(c: Category, amount: Double, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    c.emoji,
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(10.dp))
                Text(c.name, Modifier.weight(1f))
                Text(
                    formatMoney(amount),
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    "Otwórz",
                    Modifier.size(15.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}
