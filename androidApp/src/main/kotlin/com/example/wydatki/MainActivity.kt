package com.example.wydatki

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import com.example.wydatki.data.AndroidStorage
import com.example.wydatki.ui.AndroidBiometricAuthenticator
import com.example.wydatki.ui.AndroidFilePicker

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = AppStore(AndroidStorage(this))
        val authenticator = AndroidBiometricAuthenticator(this)
        val filePicker = AndroidFilePicker(this)
        
        setContent {
            App(
                store = store,
                biometricAuthenticator = authenticator,
                filePicker = filePicker
            )
        }
    }
}
