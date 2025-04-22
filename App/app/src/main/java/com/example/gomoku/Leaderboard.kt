package com.example.gomoku

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

//TODO : Afficher le classement des meilleurs joueurs de la base de données

@Composable
fun Leaderboard(pad : PaddingValues, navController: NavHostController){
    var leaderboard = listOf<String>()

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        if(leaderboard.isEmpty()){
            Text(text = "Aucun joueur")
        }
        LazyColumn {
            items(leaderboard.size){ user ->
                Text(text = leaderboard[user])
            }
        }
    }
}
