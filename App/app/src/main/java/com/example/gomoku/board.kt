package com.example.gomoku

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class CellState{
    EMPTY,
    BLACK,
    WHITE
}

data class GomokuCell(
    val x: Int,
    val y: Int,
    var state: CellState = CellState.EMPTY
)

@Composable
fun Board(
    board: List<List<GomokuCell>>,
    playerTurn: Int,
    onCellClick: (x: Int, y: Int) -> Unit
){
    val size = 15
    val cellSize = 20.dp

    Box(
        modifier = Modifier
            .size(cellSize * size)
            .background(Color(0xFFDEB887))
            .pointerInput(Unit) {
                detectTapGestures{ offset ->
                    val x = (offset.x / cellSize.toPx()).toInt()
                    val y = (offset.y / cellSize.toPx()).toInt()

                    if(x in 0 until size && y in 0 until size){
                        onCellClick(x, y)
                    }

                }
            }
    ){
        Canvas(modifier = Modifier.fillMaxSize()){
            for(i in 0 until size){
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(x = 0f, y = cellSize.toPx() * i),
                    end = androidx.compose.ui.geometry.Offset(x = size * cellSize.toPx(), y = cellSize.toPx() * i),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(x = cellSize.toPx() * i, y = 0f),
                    end = androidx.compose.ui.geometry.Offset(x = cellSize.toPx() * i, y = size * cellSize.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(x = 0f, y = cellSize.toPx() * size),
                end = androidx.compose.ui.geometry.Offset(x = size * cellSize.toPx(), y = cellSize.toPx() * size),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(x = cellSize.toPx() * size, y = 0f),
                end = androidx.compose.ui.geometry.Offset(x = cellSize.toPx() * size, y = size * cellSize.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            for (x in 0 until size) {
                for (y in 0 until size) {
                    val cell = board[x][y]
                    val state = cell.state
                    if (state != CellState.EMPTY) {
                        drawCircle(
                            color = if (state == CellState.BLACK) Color.Black else Color.White,
                            radius = (cellSize.toPx() / 2.5f),
                            center = Offset(
                                x = x * cellSize.toPx() + cellSize.toPx() / 2,
                                y = y * cellSize.toPx() + cellSize.toPx() / 2
                            )
                        )
                    }
                }
            }
        }
    }
}

fun check_win(board: List<List<GomokuCell>>, x: Int, y: Int, size: Int): Boolean{
    var nb_alignees = 1
    val color:CellState = board[x][y].state

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