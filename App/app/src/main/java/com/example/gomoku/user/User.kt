package com.example.gomoku.user

data class User(
    val email: String,
    val pseudo: String,
    val elo: Int,
    val friends: List<String>,
    val requests: List<String>,
    val profile_pic: String,
    val invitation: List<String>
)
