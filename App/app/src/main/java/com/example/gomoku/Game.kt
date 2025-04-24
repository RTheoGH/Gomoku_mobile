package com.example.gomoku

enum class GameMode{
    OFFLINE,
    ONLINE,
    ASYNCHRONOUS
}

data class Game(
    val player1: User,
    val player2: User,
    val mode: GameMode,
    val boardState: List<GomokuCell>,
    //val date: Date ?
)