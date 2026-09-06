package com.example.wydatki.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

actual fun formatMoney(amount: Double): String {
    return String.format(Locale.forLanguageTag("pl-PL"), "%.2f zł", amount)
}

actual fun getCurrentMonth(): String {
    return SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
}

actual fun getMonthLabel(month: String): String {
    val names = listOf(
        "STYCZEŃ", "LUTY", "MARZEC", "KWIECIEŃ", "MAJ", "CZERWIEC",
        "LIPIEC", "SIERPIEŃ", "WRZESIEŃ", "PAŹDZIERNIK", "LISTOPAD", "GRUDZIEŃ"
    )
    val monthIdx = month.substring(5, 7).toInt() - 1
    val year = month.substring(0, 4)
    return "${names[monthIdx]} $year"
}

actual fun shiftMonth(month: String, delta: Int): String {
    val c = Calendar.getInstance()
    c.set(month.substring(0, 4).toInt(), month.substring(5, 7).toInt() - 1, 1)
    c.add(Calendar.MONTH, delta)
    return SimpleDateFormat("yyyy-MM", Locale.US).format(c.time)
}

actual fun randomUUID(): String {
    return java.util.UUID.randomUUID().toString()
}

actual fun formatDate(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
}

actual fun getToday(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}
