package com.example.wydatki.ui

import platform.Foundation.*

actual fun formatMoney(amount: Double): String {
    val formatter = NSNumberFormatter()
    formatter.numberStyle = NSNumberFormatterDecimalStyle
    formatter.minimumFractionDigits = 2u
    formatter.maximumFractionDigits = 2u
    formatter.locale = NSLocale("pl_PL")
    val formatted = formatter.stringFromNumber(NSNumber(amount)) ?: "0,00"
    return "$formatted zł"
}

actual fun getCurrentMonth(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM"
    return formatter.stringFromDate(NSDate())
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
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    val date = formatter.dateFromString("$month-01") ?: NSDate()
    
    val calendar = NSCalendar.currentCalendar
    val offsetComponents = NSDateComponents()
    offsetComponents.month = delta.toLong()
    
    val newDate = calendar.dateByAddingComponents(offsetComponents, date, NSCalendarMatchNextTime) ?: NSDate()
    
    val outputFormatter = NSDateFormatter()
    outputFormatter.dateFormat = "yyyy-MM"
    return outputFormatter.stringFromDate(newDate)
}

actual fun randomUUID(): String {
    return NSUUID().UUIDString()
}

actual fun formatDate(millis: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(date)
}

actual fun getToday(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(NSDate())
}

actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
