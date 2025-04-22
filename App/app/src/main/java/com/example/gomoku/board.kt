package com.example.gomoku

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

enum class CellState{
    EMPTY,
    BLACK,
    WHITE
}

data class GomukuCell(
    val x: Int,
    val y: Int,
    val state: CellState = CellState.EMPTY
)

@Composable
fun board(){
    val size = 15
    val cellSize = 20.dp

    val board = remember {
        mutableStateListOf<List<GomukuCell>>().apply{
            for (i in 0 until size){
                add(List(size){ j -> GomukuCell(i, j) })
            }
        }
    }

    Box(
        modifier = Modifier
            .size(cellSize * size)
            .background(Color(0xFFDEB887))
            .pointerInput(Unit) {
                detectTapGestures{ offset ->
                    val x = (offset.x / cellSize.toPx()).toInt()
                    val y = (offset.y / cellSize.toPx()).toInt()

                    if(x in 0 until size && y in 0 until size){
                        //TODO
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