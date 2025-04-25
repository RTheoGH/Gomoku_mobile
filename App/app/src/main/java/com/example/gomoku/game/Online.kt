package com.example.gomoku.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back

@Composable
fun Online(pad : PaddingValues, navController: NavHostController){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)){
        Back(navController)
    }
    //TODO : afficher les boutons pour créer une partie ou rejoindre une partie
}

@Composable
fun Online_create(pad : PaddingValues, navController: NavHostController){
    //TODO : créer une partie en demandant le nom de la partie et mdp
}

@Composable
fun Online_join(pad : PaddingValues, navController: NavHostController){
    //TODO : rejoindre une partie en demandant le nom de la partie et mdp
}

@Composable
fun Online_lobby(pad : PaddingValues, navController: NavHostController){
    //TODO : afficher la salle d'attente avec les deux joueurs
}

@Composable
fun Online_game(pad : PaddingValues, navController: NavHostController){
    //TODO : afficher le jeu
}