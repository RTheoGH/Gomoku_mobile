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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//TODO

@Composable
fun Friends(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    // TODO : afficher les demandes d'ami de l'utilisateur
    // TODO : récupérer les amis d'un l'utilisateur depuis une base de données

    val current_user = auth.currentUser!!
    var friends by remember { mutableStateOf(listOf<String>()) }
    var requests by remember { mutableStateOf(listOf<String>()) }

    db.collection("users").document(current_user.uid).get()
            .addOnSuccessListener { res ->
                requests = res.data!!["requests"].toString().split(",")
                friends = res.data!!["friends"].toString().split(",")
                Log.i("TAG", "Requests: ${res.data!!["requests"]}")
                Log.i("TAG", "Friends: ${res.data!!["friends"]}")
            }
            .addOnFailureListener {
                Log.i("TAG", "Friends: Error")
            }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        Text(
            text = "Demandes d'amis",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            items(requests) { request ->
                Log.i("TAG", "Requests: $request")
                Text(text = request)
            }
        }

        Text(
            text = "Amis",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            items(friends) { friend ->
                Text(text = friend)
            }
        }
    }

}