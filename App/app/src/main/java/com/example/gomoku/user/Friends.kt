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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Chargement
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
    var elos = remember { mutableStateOf<Map<String, Int>>(mapOf()) }
    var pps = remember { mutableStateOf<Map<String, String>>(mapOf()) }

    var loading by remember { mutableStateOf(true) }

    fun refresh() {
        loadFriendsAndRequests(auth, db) { newFriends, newRequests ->
            friends.clear()
            friends.addAll(newFriends)
            requests.clear()
            requests.addAll(newRequests)

            if(newFriends.isEmpty()){
                loading = false
                return@loadFriendsAndRequests
            }

            val tempPps = mutableMapOf<String, String>()
            val tempElos = mutableMapOf<String, Int>()
            var loaded = 0

            newFriends.forEach { friend ->
                db.collection("users").whereEqualTo("pseudo", friend).get()
                    .addOnSuccessListener { res ->
                        val doc = res.documents.firstOrNull()
                        if(doc != null){
                            tempPps[friend] = res.documents.first().get("profile_pic").toString()
                            tempElos[friend] = res.documents.first().get("elo").toString().toInt()
                        }
                        loaded++
                        if(loaded == newFriends.size){
                            pps.value = tempPps
                            elos.value = tempElos
                            loading = false
                        }
                    }
                    .addOnFailureListener {
                        loaded++
                        if(loaded == newFriends.size){
                            loading = false
                        }
                    }
            }
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Text(
            text = stringResource(R.string.requests),
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(4.dp)
        )
        if(loading) Chargement()
        else{
            if(requests.isEmpty()){
                Text(
                    text = stringResource(R.string.no_requests),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(4.dp)
                )
            }else{
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(requests) { request ->
                        Log.i("TAG", "Requests: $request")
                        if(loading) Chargement()
                        else Recup_request(request,auth,db, onRefresh = { refresh() })
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.friends),
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(4.dp)
        )

        if(loading) Chargement()
        else{
            if(friends.isEmpty()){
                Text(
                    text = stringResource(R.string.no_friends),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(4.dp)
                )
            }else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(friends) { friend ->
                        Log.i("TAG", "Friends: $friend")
                        if (loading) Chargement()
                        else Recup_friend(friend, pps, elos, auth, db, onRefresh = { refresh() })
                    }
                }
            }
        }

    }
}