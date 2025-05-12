package com.example.gomoku

import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gomoku.nav.Navigation
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase,
    intent: Intent,
    navController: NavHostController = rememberNavController()
){

    val type = intent.getStringExtra("notification_type")

    LaunchedEffect(type){
        when (type) {
            "invitation" -> {
                val inviter = intent.getStringExtra("inviter")
                val lobbyId = intent.getStringExtra("lobbyId")
                if(inviter != null && lobbyId != null){
                    joinLobbyAndRemoveInvitation(inviter,lobbyId)
                    navController.navigate(Screens.Online_lobby.name + "/$lobbyId")
                }
            }
            "request" -> {
                navController.navigate(Screens.Friends.name)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Gomoku") }
            )
        },
    ) { innerPadding ->
        Navigation(innerPadding, navController, Screens.Menu.name, auth, db, rdb)
    }
}