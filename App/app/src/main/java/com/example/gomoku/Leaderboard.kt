package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Leaderboard(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    var leaderboard by remember { mutableStateOf(listOf<User>()) }

    LaunchedEffect(Unit) {
        db.collection("users").orderBy("elo").get()
            .addOnSuccessListener { res ->
                val users = res.documents.mapNotNull { doc ->
                    val pseudo = doc.data?.get("pseudo").toString()
                    val email = doc.data?.get("email").toString()
                    val elo = doc.data?.get("elo")
                    User(email, pseudo, elo.toString().toInt())
                }
                leaderboard = users
            }
            .addOnFailureListener {
                Log.i("TAG", "Leaderboard: Error")
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        Text(
            text = "Leaderboard",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(leaderboard){ index,user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
                        .background(
                            if (index == 0) Color(0xFFFFD700) else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "${index+1}",
                        fontSize = 20.sp,
                        color = if (index == 0) Color.Black else Color.Gray,
                        modifier = Modifier.width(40.dp)
                    )

                    Text(
                        text = user.pseudo,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f).clickable { /*TODO : Afficher le profil de l'utilisateur*/ }
                    )

                    Text(
                        text = "${user.elo}",
                        fontSize = 18.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}
