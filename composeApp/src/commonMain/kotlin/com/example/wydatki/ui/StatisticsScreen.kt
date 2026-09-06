package com.example.wydatki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wydatki.AppStore
import com.example.wydatki.ui.components.Empty
import com.example.wydatki.ui.components.ExpensePieChart
import com.example.wydatki.ui.components.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(store: AppStore, month: String, setMonth: (String) -> Unit, back: () -> Unit) {
    var viewMode by remember { mutableStateOf("year") }
    var selectedYear by remember { mutableIntStateOf(month.substring(0, 4).toInt()) }
    
    var startDate by remember { mutableStateOf(getToday()) }
    var endDate by remember { mutableStateOf(getToday()) }
    var datePickerTarget by remember { mutableStateOf<String?>(null) }

    val expenses = if (viewMode == "year") {
        store.expenses.filter { monthOf(it.date).startsWith("$selectedYear-") }
    } else {
        store.expenses.filter { it.date in startDate..endDate }
    }
    
    val income = if (viewMode == "year") {
        store.incomes.filter { monthOf(it.date).startsWith("$selectedYear-") }.sumOf { it.amount }
    } else {
        store.incomes.filter { it.date in startDate..endDate }.sumOf { it.amount }
    }
    
    val total = expenses.sumOf { it.amount }

    val grouped = store.categories.map { category ->
        category to expenses.filter { it.categoryId == category.id }.sumOf { it.amount }
    }.filter { it.second > 0 }.sortedByDescending { it.second }

    val monthExpenses = store.expenses.filter { monthOf(it.date) == month }
    val monthTotal = monthExpenses.sumOf { it.amount }
    val monthIncome = store.incomes.filter { monthOf(it.date) == month }.sumOf { it.amount }
    
    val avg = if (expenses.isEmpty()) 0.0
    else total / expenses.map { it.date }.distinct().size

    Column(Modifier.fillMaxSize().background(Color(0xFFF6F8F7))) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć") }
            Text(
                "STATYSTYKI",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Row(
                Modifier
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                    .padding(2.dp)
            ) {
                listOf("year" to Icons.Default.CalendarMonth, "period" to Icons.Default.BarChart).forEach { (m, icon) ->
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (viewMode == m) Color.White else Color.Transparent)
                            .clickable { viewMode = m }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            icon,
                            null,
                            Modifier.size(18.dp),
                            tint = if (viewMode == m) Color(0xFF278B68) else Color.Gray
                        )
                    }
                }
            }
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        if (viewMode == "year") {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ROK", fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton({ selectedYear-- }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Poprzedni rok")
                                    }
                                    Text(
                                        "$selectedYear",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton({ selectedYear++ }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, "Następny rok")
                                    }
                                }
                            }
                        } else {
                            Text("WYBRANY OKRES", fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = startDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.weight(1f).clickable { datePickerTarget = "start" },
                                    label = { Text("Od") },
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.Black,
                                        disabledBorderColor = Color.LightGray,
                                        disabledLabelColor = Color.Gray
                                    )
                                )
                                OutlinedTextField(
                                    value = endDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.weight(1f).clickable { datePickerTarget = "end" },
                                    label = { Text("Do") },
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.Black,
                                        disabledBorderColor = Color.LightGray,
                                        disabledLabelColor = Color.Gray
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(if (viewMode == "year") "SUMA ROKU" else "SUMA OKRESU", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            formatMoney(total),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            "Dochód: ${formatMoney(income)}  •  Bilans: ${formatMoney(income - total)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (viewMode == "year") {
                item {
                    Text("MIESIĄCE • $selectedYear", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                items((1..12).toList()) { monthNumber ->
                    val ym = "$selectedYear-${monthNumber.toString().padStart(2, '0')}"
                    val mExpenses = store.expenses.filter { monthOf(it.date) == ym }.sumOf { it.amount }
                    val mIncome = store.incomes.filter { monthOf(it.date) == ym }.sumOf { it.amount }
                    val active = ym == month

                    Card(
                        Modifier.fillMaxWidth().clickable { setMonth(ym) },
                        colors = CardDefaults.cardColors(if (active) Color(0xFFE8F4EE) else Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                getMonthLabel(ym).replace("$selectedYear", "").trim(),
                                Modifier.weight(1f),
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatMoney(mExpenses), fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                if (mIncome > 0) Text(
                                    "dochód ${formatMoney(mIncome)}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF087443)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    if (viewMode == "year") "SZCZEGÓŁY • ${getMonthLabel(month)}" else "SZCZEGÓŁY OKRESU",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }

            if (viewMode == "year") {
                item {
                    SummaryCard(
                        Modifier.fillMaxWidth(),
                        "WYDATKI",
                        monthTotal,
                        Color(0xFFFCE7E5),
                        Color(0xFFC62828)
                    )
                }
                item {
                    SummaryCard(
                        Modifier.fillMaxWidth(),
                        "ŚREDNIO / DZIEŃ",
                        if (monthExpenses.isEmpty()) 0.0 else monthTotal / monthExpenses.map { it.date }.distinct().size,
                        Color(0xFFE5F1F8),
                        Color(0xFF156082)
                    )
                }
                item {
                    SummaryCard(
                        Modifier.fillMaxWidth(),
                        "DOCHÓD",
                        monthIncome,
                        Color(0xFFE2F4E9),
                        Color(0xFF087443)
                    )
                }
            } else {
                item {
                    SummaryCard(
                        Modifier.fillMaxWidth(),
                        "ŚREDNIO / DZIEŃ",
                        avg,
                        Color(0xFFE5F1F8),
                        Color(0xFF156082)
                    )
                }
            }

            item {
                Text("PODZIAŁ WYDATKÓW", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            val displayGrouped = if (viewMode == "year") {
                store.categories.map { category ->
                    category to monthExpenses.filter { it.categoryId == category.id }.sumOf { it.amount }
                }.filter { it.second > 0 }.sortedByDescending { it.second }
            } else {
                grouped
            }
            
            val displayTotal = if (viewMode == "year") monthTotal else total

            if (displayGrouped.isNotEmpty()) {
                item {
                    ExpensePieChart(
                        grouped = displayGrouped,
                        total = displayTotal
                    )
                }
                items(displayGrouped) { (c, v) ->
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row {
                                Text(
                                    "${c.emoji} ${c.name}",
                                    Modifier.weight(1f)
                                )
                                Text(formatMoney(v), fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(5.dp))
                            Text(
                                "${if (displayTotal > 0) (v / displayTotal * 100).toInt() else 0}% wszystkich wydatków",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                item { Empty("Brak wydatków.") }
            }
        }
    }
    
    if (datePickerTarget != null) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { datePickerTarget = null },
            confirmButton = {
                TextButton({
                    state.selectedDateMillis?.let {
                        val d = formatDate(it)
                        if (datePickerTarget == "start") startDate = d else endDate = d
                    }
                    datePickerTarget = null
                }) { Text("OK") }
            }
        ) {
            DatePicker(state)
        }
    }
}
