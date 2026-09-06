package com.example.wydatki.ui

interface FilePicker {
    fun exportJson(content: String, fileName: String, onResult: (Boolean, String) -> Unit)
    fun importJson(onResult: (String?) -> Unit)
}
