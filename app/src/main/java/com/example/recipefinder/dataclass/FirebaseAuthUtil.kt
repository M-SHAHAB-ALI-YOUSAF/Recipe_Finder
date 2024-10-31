package com.example.recipefinder.dataclass

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }
}