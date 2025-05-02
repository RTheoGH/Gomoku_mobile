package com.example.gomoku.user

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Recup_request
import com.example.gomoku.Recup_friend
import com.example.gomoku.loadFriendsAndRequests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//TODO

@Composable
fun Friends(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    // TODO : afficher les demandes d'ami de l'utilisateur
    // TODO : récupérer les amis d'un l'utilisateur depuis une base de données

    val current_user = auth.currentUser!!
    var friends = remember { mutableStateListOf<String>() }
    var requests = remember { mutableStateListOf<String>() }

    fun refresh() {
        loadFriendsAndRequests(auth, db) { newFriends, newRequests ->
            friends.clear()
            friends.addAll(newFriends)
            requests.clear()
            requests.addAll(newRequests)
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(pad)
            .padding(8.dp)
    ) {
        Back(navController)

        Text(
            text = "Demandes d'amis",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(requests) { request ->
                Log.i("TAG", "Requests: $request")
                Recup_request(request,auth,db, onRefresh = { refresh() })
            }
        }

        Text(
            text = "Amis",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(friends) { friend ->
                Log.i("TAG", "Friends: $friend")
                Recup_friend(friend,auth,db, onRefresh = { refresh() })
            }
        }
    }
}