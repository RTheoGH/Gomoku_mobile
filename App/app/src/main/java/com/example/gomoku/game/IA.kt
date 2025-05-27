package com.example.gomoku.game

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Custom_row
import com.example.gomoku.R
import com.example.gomoku.nav.Screens

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Offline_game_IA(pad : PaddingValues, navController: NavHostController, player1: String, player2: String, choice: String){
    val context = LocalContext.current

    Log.i("TAG", "Offline_game: $player1, $player2")
    var showDialog by remember { mutableStateOf(false) }

    val size = 15
    var playerTurn by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var board = remember {
        mutableStateListOf<List<GomokuCell>>().apply{
            for (i in 0 until size){
                add(List(size){ j -> GomokuCell(i, j) })
            }
        }
    }
    var turn_history = remember { mutableStateListOf(context.getString(R.string.game_start_message)) }
    val listState = rememberLazyListState()
    if(playerTurn == 1 && !isFinished){
        val coupAI = when{
            choice == "Easy" -> play_random(board)
            choice == "Medium" -> play_ia_medium(board)
            choice == "Hard" -> play_ia_hard(board)
            else -> play_random(board)
        }
        if (coupAI != null) {
            board[coupAI.first] = board[coupAI.first].toMutableList().apply {
                this[coupAI.second] = board[coupAI.first][coupAI.second].copy(state = CellState.BLACK)
            }
            val player = if(playerTurn == 0) player1 else player2
            val pos_x = coupAI.first+1
            val pos_y = coupAI.second+1

            turn_history.add(player+" "+context.getString(R.string.played_in)+" "+pos_x+","+pos_y+".")
            println(turn_history)

            if (check_win(board, coupAI.first, coupAI.second, size)) {
                isFinished = true
                showDialog = true
                println("gagné !!!!!!")
            }

            playerTurn = 1 - playerTurn
        }
    }
    LaunchedEffect(turn_history.size) {
        listState.animateScrollToItem(turn_history.size - 1)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ){
        Back(navController)

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Custom_row(1,"",player2,"")
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Board(
                board = board,
                playerTurn = playerTurn,
                onCellClick = { x, y ->
                    if (board[x][y].state == CellState.EMPTY && !isFinished && playerTurn == 0) {
                        val newState = CellState.WHITE
                        board[x] = board[x].toMutableList().apply {
                            this[y] = board[x][y].copy(state = newState)
                        }
                        val player = if(playerTurn == 0) player1 else player2
                        val pos_x = x+1
                        val pos_y = y+1

                        turn_history.add(player+" "+context.getString(R.string.played_in)+" "+pos_x+","+pos_y+".")
                        println(turn_history)

                        if (check_win(board, x, y, size)) {
                            isFinished = true
                            showDialog = true
                            println("gagné !!!!!!")
                        }

                        playerTurn = 1 - playerTurn
                        println("Tour du joueur : $playerTurn")
                    }
                }
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Custom_row(2,"",player1,"")

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            LazyColumn(state = listState, modifier = Modifier.background(Color.LightGray).fillMaxWidth().padding(vertical = 10.dp)) {
                items(turn_history){ turn ->
                    Text(modifier = Modifier.padding(horizontal = 5.dp), text = turn)
                }
                if (isFinished){
                    item {
                        val player = if(playerTurn == 1) player1 else player2
                        Text(modifier = Modifier.padding(horizontal = 5.dp), text = player+" "+stringResource(R.string.win))
                    }
                }
            }

            if(showDialog){
                val winner = if(playerTurn == 1) player1 else player2
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = stringResource(R.string.game_over)) },
                    text = { Text(text = winner+" "+stringResource(R.string.win)) },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            val route = "${Screens.Offline_game.name}/$player1/$player2/true/$choice"
                            navController.navigate(route){
                                popUpTo(route){
                                    inclusive = true
                                }
                            }
                        }) {
                            Text(text = stringResource(R.string.replay))
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog = false
                            navController.navigate(Screens.Menu.name){
                                popUpTo(Screens.Menu.name){
                                    inclusive = true
                                }
                            }
                        }) {
                            Text(text = stringResource(R.string.leave))
                        }
                    }
                )
            }
        }
    }
}