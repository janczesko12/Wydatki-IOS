package com.example.wydatki.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore

sealed interface CloudPullResult {
    data class Data(val json: String) : CloudPullResult
    data object DocumentMissing : CloudPullResult
    data class Error(val cause: Throwable) : CloudPullResult
}

object FirebaseSync {
    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    val userId: String?
        get() = auth.currentUser?.uid

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun push(json: String) {
        val uid = userId ?: return
        val doc = db.collection("wydatki").document(uid)
        doc.set(
            mapOf(
                "data" to json,
                "updatedAt" to Timestamp.now()
            )
        )
    }

    suspend fun pull(): CloudPullResult {
        val uid = userId ?: return CloudPullResult.Error(
            IllegalStateException("Brak zalogowanego użytkownika")
        )

        return try {
            val snapshot = db.collection("wydatki").document(uid).get()
            if (snapshot.exists) {
                val data = snapshot.get<String>("data")
                if (data != null) {
                    CloudPullResult.Data(data)
                } else {
                    CloudPullResult.Error(
                        IllegalStateException("Dokument istnieje, ale nie zawiera pola data")
                    )
                }
            } else {
                CloudPullResult.DocumentMissing
            }
        } catch (e: Exception) {
            CloudPullResult.Error(e)
        }
    }
}
