package com.example.gomoku.game

import com.example.gomoku.user.User

enum class GameMode{
    PUBLIC,
    PRIVATE,
    ASYNCHRONOUS
}

data class Game(
    val player1: User,
    val player2: User,
    val mode: GameMode,
    val boardState: List<GomokuCell>,
    //val date: Date ?
)

fun check_win(board: List<List<GomokuCell>>, x: Int, y: Int, size: Int): Boolean{
    var nb_alignees = 1
    val color: CellState = board[x][y].state

    //diagonal \
    var currentX = x-1
    var currentY = y-1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX--
        currentY--
        if(nb_alignees>=5) return true
    }
    currentX = x+1
    currentY = y+1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX++
        currentY++
        if(nb_alignees>=5) return true
    }

    //diagonal /
    nb_alignees = 1
    currentX = x-1
    currentY = y+1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX--
        currentY++
        if(nb_alignees>=5) return true
    }
    currentX = x+1
    currentY = y-1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX++
        currentY--
        if(nb_alignees>=5) return true
    }

    //horizontal -
    nb_alignees = 1
    currentX = x-1
    currentY = y
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX--
        if(nb_alignees>=5) return true
    }
    currentX = x+1
    currentY = y
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentX++
        if(nb_alignees>=5) return true
    }

    //vertical |
    nb_alignees = 1
    currentX = x
    currentY = y+1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentY++
        if(nb_alignees>=5) return true
    }
    currentX = x
    currentY = y-1
    while(currentX in 0..<size && currentY in 0..<size && board[currentX][currentY].state == color){
        nb_alignees++
        currentY--
        if(nb_alignees>=5) return true
    }

    return false
}