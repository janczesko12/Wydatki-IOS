package com.example.wydatki

import androidx.compose.ui.window.ComposeUIViewController
import com.example.wydatki.data.IosStorage
import com.example.wydatki.ui.IosBiometricAuthenticator
import com.example.wydatki.ui.IosFilePicker

fun MainViewController() = ComposeUIViewController {
    val store = AppStore(IosStorage())
    val authenticator = IosBiometricAuthenticator()
    val filePicker = IosFilePicker()
    
    App(
        store = store,
        biometricAuthenticator = authenticator,
        filePicker = filePicker
    )
}
