package com.example.wydatki.ui

interface BiometricAuthenticator {
    suspend fun authenticate(reason: String): Boolean
    fun isAvailable(): Boolean
}
