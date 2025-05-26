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

fun play_random(board: List<List<GomokuCell>>) : Pair<Int, Int>?{
    val size = board.size
    val emptyCells = mutableListOf<Pair<Int, Int>>()

    for (i in 0 until size) {
        for (j in 0 until size) {
            if (board[i][j].state == CellState.EMPTY) {
                emptyCells.add(Pair(i, j))
            }
        }
    }
    if (emptyCells.isEmpty()) return null

    return emptyCells.random()
}

fun get_nb_points(board: List<List<GomokuCell>>, x: Int, y: Int, playerState: CellState): Int {
    if (board[x][y].state != CellState.EMPTY) return 0

    val directions = listOf(
        Pair(1, 0),   // horizontal
        Pair(0, 1),   // vertical
        Pair(1, 1),   // diagonal \
        Pair(1, -1)   // diagonal /
    )

    var maxPoints = 0

    for ((dx, dy) in directions) {
        var count = 1  // On compte la pierre qu'on va jouer

        // direction +
        var nx = x + dx
        var ny = y + dy
        while (
            nx in board.indices &&
            ny in board.indices &&
            board[nx][ny].state == playerState
        ) {
            count++
            nx += dx
            ny += dy
        }

        // direction -
        nx = x - dx
        ny = y - dy
        while (
            nx in board.indices &&
            ny in board.indices &&
            board[nx][ny].state == playerState
        ) {
            count++
            nx -= dx
            ny -= dy
        }

        if (count > maxPoints) maxPoints = count
    }

    return maxPoints
}

fun play_ia_medium(board: List<List<GomokuCell>>) : Pair<Int, Int>? {
    var maxPts = 1
    var maxCase = play_random(board)
    for (i in 0 until board.size) {
        for (j in 0 until board.size) {
            if (board[i][j].state == CellState.EMPTY) {
                val currentPts = get_nb_points(board, i, j, CellState.BLACK)
                if (currentPts > maxPts) {
                    maxPts = currentPts
                    maxCase = Pair(i, j)
                }

            }
        }
    }
    println("Meilleur coup : $maxPts")
    println("Meilleure case : $maxCase")
    return maxCase
}

fun evaluate_move(
    board: List<List<GomokuCell>>,
    x: Int, y: Int,
    aiState: CellState,
    enemyState: CellState
): Int {
    if (board[x][y].state != CellState.EMPTY) return -1

    val attackPoints = get_nb_points(board, x, y, aiState)
    val defensePoints = get_nb_points(board, x, y, enemyState)

    return (attackPoints * 2) + (defensePoints * 3)
}


fun play_ia_impossible(board: List<List<GomokuCell>>, aiState: CellState): Pair<Int, Int>? {
    val enemyState = if (aiState == CellState.BLACK) CellState.WHITE else CellState.BLACK

    var bestScore = 1
    var bestMove: Pair<Int, Int>? = play_random(board)

    for (x in board.indices) {
        for (y in board.indices) {
            val score = evaluate_move(board, x, y, aiState, enemyState)
            if (score > bestScore) {
                bestScore = score
                bestMove = Pair(x, y)
            }
        }
    }

    println("Coup IA choisi : $bestMove avec score $bestScore")
    return bestMove
}
