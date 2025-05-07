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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.R
import com.example.gomoku.Recup_request
import com.example.gomoku.Recup_friend
import com.example.gomoku.loadFriendsAndRequests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Friends(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){

    var friends = remember { mutableStateListOf<String>() }
    var requests = remember { mutableStateListOf<String>() }
    var elos = remember { mutableMapOf<String, Int>() }
    var pps = remember { mutableMapOf<String, String>() }

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

    friends.forEach { friend ->
        db.collection("users").whereEqualTo("pseudo", friend).get()
            .addOnSuccessListener { res ->
                pps[friend] = res.documents.first().get("profile_pic").toString()
                elos[friend] = res.documents.first().get("elo").toString().toInt()
            }
            .addOnFailureListener {
                Log.i("TAG", "Friends: Error getting elo")
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Text(
            text = stringResource(R.string.requests),
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
            text = stringResource(R.string.friends),
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
                Recup_friend(friend,pps,elos,auth,db, onRefresh = { refresh() })
            }
        }
    }
}