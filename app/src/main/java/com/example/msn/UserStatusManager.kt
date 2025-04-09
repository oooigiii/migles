package com.example.msn

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class UserStatusManager {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun setOnline() {
        auth.currentUser?.let { user ->
            val userStatusRef = database.reference
                .child("status")
                .child(user.uid)

            val status = mapOf(
                "state" to "online",
                "lastSeen" to ServerValue.TIMESTAMP
            )

            userStatusRef.onDisconnect().setValue(mapOf("state" to "offline"))
            userStatusRef.setValue(status)
        }
    }

    fun setOffline() {
        auth.currentUser?.let { user ->
            val userStatusRef = database.reference
                .child("status")
                .child(user.uid)

            userStatusRef.setValue(mapOf(
                "state" to "offline",
                "lastSeen" to ServerValue.TIMESTAMP
            ))
        }
    }
}
