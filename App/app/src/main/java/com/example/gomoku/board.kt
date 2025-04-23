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

        }
    }
}

fun check_win(board: List<List<GomokuCell>>, x: Int, y: Int, size: Int): Boolean{
    var nb_alignees = 1
    val color:CellState = board[x][y].state

    //diagonal \
    var current_x = x-1
    var current_y = y-1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x--
        current_y--
        if(nb_alignees>=5) return true
    }
    current_x = x+1
    current_y = y+1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x++
        current_y++
        if(nb_alignees>=5) return true
    }

    //diagonal /
    current_x = x-1
    current_y = y+1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x--
        current_y++
        if(nb_alignees>=5) return true
    }
    current_x = x+1
    current_y = y-1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x++
        current_y--
        if(nb_alignees>=5) return true
    }

    //horizontal -
    current_x = x-1
    current_y = y
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x--
        if(nb_alignees>=5) return true
    }
    current_x = x+1
    current_y = y
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_x++
        if(nb_alignees>=5) return true
    }

    //vertical |
    current_x = x
    current_y = y+1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_y++
        if(nb_alignees>=5) return true
    }
    current_x = x
    current_y = y-1
    while(current_x in 0..<size && current_y in 0..<size && board[current_x][current_y].state == color){
        nb_alignees++
        current_y--
        if(nb_alignees>=5) return true
    }

    return false
}