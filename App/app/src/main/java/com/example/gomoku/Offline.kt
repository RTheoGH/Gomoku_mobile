package com.example.gomoku

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                value = "",
                onValueChange = { player1 = it },
                label = { Text(text = "Joueur 1") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { player2 = it },
                label = { Text(text = "Joueur 2") },
                modifier = Modifier.padding(4.dp)
            )

            Button(
                onClick = {
                    //TODO
                    navController.navigate(Screens.Offline_game.name)
                },
            ){
                Text(text = "Jouer")
            }
        }
    }
}

@Composable
fun Offline_game(pad : PaddingValues, navController: NavHostController){
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
            Custom_row(1,"02:00","Joueur 1")
            board()
            Custom_row(2,"02:00","Joueur 2")
        }
    }
}


