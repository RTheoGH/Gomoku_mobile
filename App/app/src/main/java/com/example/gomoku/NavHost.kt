package com.example.gomoku

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Navigation(pad : PaddingValues, navController: NavHostController, startDestination: String, auth: FirebaseAuth, db: FirebaseFirestore){
    NavHost(navController = navController, startDestination = startDestination){
        composable(route = Screens.Menu.name) {
            Menu(pad, navController, auth, db)
        }

        composable(route = Screens.Sign_in.name) {
            Sign_in(pad, navController, auth, db)
        }
        composable(route = Screens.Sign_up.name) {
            Sign_up(pad, navController, auth, db)
        }

        composable(route = Screens.Profile.name) {
            Profile(pad, navController, auth, db)
        }
        composable(route = Screens.EditProfile.name) {
            EditProfile(pad, navController, auth, db)
        }

        composable(route = Screens.Leaderboard.name) {
            Leaderboard(pad, navController, db)
        }
        composable(route = Screens.Friends.name) {
            Friends(pad, navController)
        }

        composable(route = Screens.Offline_lobby.name) {
            Offline_lobby(pad, navController)
        }
        composable(route = Screens.Offline_game.name) {
            Offline_game(pad, navController)
        }

        composable(route = Screens.Online.name) {
            Online(pad, navController)
        }
        composable(route = Screens.Online_create.name) {
            Online_create(pad, navController)
        }
        composable(route = Screens.Online_join.name) {
            Online_join(pad, navController)
        }
        composable(route = Screens.Online_lobby.name) {
            Online_lobby(pad, navController)
        }
        composable(route = Screens.Online_game.name) {
            Online_game(pad, navController)
        }

        composable(route = Screens.Asynchronus.name) {
            Asynchronus(pad, navController)
        }
        composable(route = Screens.Asynchronus_create.name) {
            Asynchronus_create(pad, navController)
        }
        composable(route = Screens.Asynchronus_join.name) {
            Asynchronus_join(pad, navController)
        }
        composable(route = Screens.Asynchronus_lobby.name) {
            Asynchronus_lobby(pad, navController)
        }
        composable(route = Screens.Asynchronus_game.name) {
            Asynchronus_game(pad, navController)
        }
    }
}