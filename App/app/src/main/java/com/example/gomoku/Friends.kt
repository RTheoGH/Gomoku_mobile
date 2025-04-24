package com.example.gomoku

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

//TODO

@Composable
fun Friends(pad : PaddingValues, navController: NavHostController){
    // TODO : afficher les demandes d'ami de l'utilisateur
    // TODO : récupérer les amis d'un l'utilisateur depuis une base de données

    var friends = listOf<String>()

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        if(friends.isEmpty()){
            Text(
                text = "Aucun ami :(",
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            items(friends) { friend ->
                Text(text = friend)
            }
        }
    }

}