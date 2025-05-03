package com.example.gomoku.game

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.R
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Online(pad : PaddingValues, navController: NavHostController){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)){
        Back(navController)

        Spacer(modifier = Modifier.height(164.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(Screens.Online_create.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "créé")
            }
            Button(
                onClick = {
                    navController.navigate(Screens.Online_join.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "rejoint")
            }
        }
    }
    //TODO : afficher les boutons pour créer une partie ou rejoindre une partie
}

@Composable
fun Online_create(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, rdb: FirebaseDatabase){
    var lobby_name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Spacer(modifier = Modifier.height(164.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lobby_name,
                onValueChange = { if(it.length <= 10) lobby_name = it },
                label = { Text(text = "Nom de la partie") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if(it.length <= 10) password = it },
                label = { Text(text = "Mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            Button(
                onClick = {

                    navController.navigate(Screens.Online_lobby.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "Creer")
            }
        }
    }
    //TODO : créer une partie en demandant le nom de la partie et mdp
}

@Composable
fun Online_join(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, rdb: FirebaseDatabase){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)
    }
    //TODO : rejoindre une partie en demandant le nom de la partie et mdp
}

@Composable
fun Online_lobby(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, rdb: FirebaseDatabase){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)
    }
    //TODO : afficher la salle d'attente avec les deux joueurs
}

@Composable
fun Online_game(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, rdb: FirebaseDatabase){
    //TODO : afficher le jeu
}