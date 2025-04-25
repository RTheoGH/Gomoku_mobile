package com.example.gomoku

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Offline_lobby(pad : PaddingValues, navController: NavHostController){
    var player1 by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ){
        Back(navController)

        Spacer(modifier = Modifier.height(112.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Mode : Offline")

            OutlinedTextField(
                value = player1,
                onValueChange = { if(it.length <= 10) player1 = it },
                label = { Text(text = "Joueur 1") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = player2,
                onValueChange = { if(it.length <= 10) player2 = it },
                label = { Text(text = "Joueur 2") },
                modifier = Modifier.padding(4.dp)
            )

            Button(
                onClick = {
                    //TODO
                    val route = "${Screens.Offline_game.name}/$player1/$player2"
                    navController.navigate(route)
                },
            ){
                Text(text = "Jouer")
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Offline_game(pad : PaddingValues, navController: NavHostController, player1: String, player2: String){
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
    var turn_history = remember { mutableStateListOf("Début de la partie") }
    val listState = rememberLazyListState()
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
            Custom_row(1,"",player2)
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Board(
                board = board,
                playerTurn = playerTurn,
                onCellClick = { x, y ->
                    if (board[x][y].state == CellState.EMPTY && !isFinished) {
                        val newState = if (playerTurn == 0) CellState.WHITE else CellState.BLACK
                        board[x] = board[x].toMutableList().apply {
                            this[y] = board[x][y].copy(state = newState)
                        }
                        val player = if(playerTurn == 0) player1 else player2
                        turn_history.add("$player a joué en $x, $y.")
                        println(turn_history)

                        if (check_win(board, x, y, size)) {
                            isFinished = true
                            showDialog = true
                            println("gagné !!!!!!")
                            //TODO : enregistrer la partie dans l'historique
                        }

                        playerTurn = 1 - playerTurn
                        println("Tour du joueur : $playerTurn")
                    }
                }
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Custom_row(2,"",player1)

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            LazyColumn(state = listState, modifier = Modifier.background(Color.LightGray).fillMaxWidth().padding(vertical = 10.dp)) {
                items(turn_history){ turn ->
                    Text(modifier = Modifier.padding(horizontal = 5.dp), text = turn)
                }
                if (isFinished){
                    item {
                        val player = if(playerTurn == 1) player1 else player2
                        Text(modifier = Modifier.padding(horizontal = 5.dp), text = "$player a gagné la partie !")
                    }
                }
            }

            if(showDialog){
                val winner = if(playerTurn == 1) player1 else player2
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Partie terminée") },
                    text = { Text(text = "$winner a gagné la partie !") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            val route = "${Screens.Offline_game.name}/$player1/$player2"
                            navController.navigate(route){
                                popUpTo(route){
                                    inclusive = true
                                }
                            }
                        }) {
                            Text(text = "Rejouer")
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
                            Text(text = "Quitter")
                        }
                    }
                )
            }
        }
    }
}