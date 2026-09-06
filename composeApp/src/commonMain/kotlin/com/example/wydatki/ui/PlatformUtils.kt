package com.example.wydatki.ui

expect fun formatMoney(amount: Double): String
expect fun getCurrentMonth(): String
expect fun getMonthLabel(month: String): String
expect fun shiftMonth(month: String, delta: Int): String
expect fun randomUUID(): String
expect fun formatDate(millis: Long): String
expect fun getToday(): String
expect fun getCurrentTimeMillis(): Long

fun monthOf(date: String) = date.take(7)
