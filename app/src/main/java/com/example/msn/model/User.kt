package com.example.msn.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val status: String = "offline",
    val lastSeen: Long = 0
)
