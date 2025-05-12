package com.example.gomoku.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gomoku.user.EditProfile
import com.example.gomoku.user.Friends
import com.example.gomoku.user.Leaderboard
import com.example.gomoku.Menu
import com.example.gomoku.user.Profile
import com.example.gomoku.user.Sign_in
import com.example.gomoku.user.Sign_up
import com.example.gomoku.game.Offline_game
import com.example.gomoku.game.Offline_lobby
import com.example.gomoku.game.Online
import com.example.gomoku.game.Online_create
import com.example.gomoku.game.Online_game
import com.example.gomoku.game.Online_join
import com.example.gomoku.game.Online_lobby
import com.example.gomoku.game.Online_matchmaking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Navigation(pad : PaddingValues, navController: NavHostController, startDestination: String, auth: FirebaseAuth, db: FirebaseFirestore, rdb: FirebaseDatabase){
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

        composable(
            route = "${Screens.Profile.name}/{pseudo}",
            arguments = listOf(navArgument("pseudo") { nullable = false })
        ) { backStackEntry ->
            val pseudo = backStackEntry.arguments?.getString("pseudo") ?: ""
            Profile(pad, navController, auth, db, pseudo)
        }
        composable(route = Screens.EditProfile.name) {
            EditProfile(pad, navController, auth, db)
        }

        composable(route = Screens.Leaderboard.name) {
            Leaderboard(pad, navController, db)
        }
        composable(route = Screens.Friends.name) {
            Friends(pad, navController, auth, db)
        }

        composable(route = Screens.Offline_lobby.name) {
            Offline_lobby(pad, navController)
        }
        composable(
            route = "${Screens.Offline_game.name}/{player1}/{player2}",
            arguments = listOf(
                navArgument("player1") { nullable = false },
                navArgument("player2") { nullable = false }
            )
        ) { backStackEntry ->
            val player1 = backStackEntry.arguments?.getString("player1") ?: ""
            val player2 = backStackEntry.arguments?.getString("player2") ?: ""
            Offline_game(pad, navController, player1 = player1, player2 = player2)
        }

        composable(route = Screens.Online.name) {
            Online(pad, navController)
        }
        composable(route = Screens.Online_matchmaking.name) {
            Online_matchmaking(pad, navController, auth, db, rdb)
        }
        composable(route = Screens.Online_create.name) {
            Online_create(pad, navController, auth, db, rdb)
        }
        composable(route = Screens.Online_join.name) {
            Online_join(pad, navController, auth, db, rdb)
        }
        composable(
            route = "${Screens.Online_lobby.name}/{lobbyId}",
            arguments = listOf(
                navArgument("lobbyId") { nullable = false }
            )
        ) { backStackEntry ->
            val lobbyId = backStackEntry.arguments?.getString("lobbyId") ?: ""
            Online_lobby(pad, navController,auth,db,rdb,lobbyId=lobbyId)
        }
        composable(
            route = "${Screens.Online_game.name}/{lobbyId}",
            arguments = listOf(
                navArgument("lobbyId") { nullable = false }
            )
        ) { backStackEntry ->
            val lobbyId = backStackEntry.arguments?.getString("lobbyId") ?: ""
            Online_game(pad, navController,auth,db,rdb,lobbyId=lobbyId)
        }
    }
}