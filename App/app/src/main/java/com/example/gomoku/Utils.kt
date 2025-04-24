package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Back(navController: NavHostController){
    IconButton(
        onClick = { navController.navigate(Screens.Menu.name) },
        modifier = Modifier.padding(4.dp).size(32.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.padding(4.dp).size(32.dp)
        )
    }
}

@Composable
fun Custom_row(i : Int, timer : String, player : String){
    // TODO : remember players name & update timers
    Row(
        modifier = if(i == 1) Modifier.fillMaxWidth().padding(end = 16.dp)
        else if(i == 2) Modifier.fillMaxWidth().padding(start = 16.dp)
        else Modifier.fillMaxWidth(),
        horizontalArrangement = if(i == 1) Arrangement.End
        else if(i == 2) Arrangement.Start
        else Arrangement.Center,
    ){
        if(i == 1){
            Custom_card(timer)
            Custom_card(player)
        }
        else if(i == 2){
            Custom_card(player)
            Custom_card(timer)
        }
    }
}

@Composable
fun Custom_card(text : String){
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun recup_moi(auth: FirebaseAuth, db: FirebaseFirestore, onRes : (String) -> Unit) {
    var current_user = auth.currentUser ?: return onRes("")

    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            val pseudo = res.getString("pseudo") ?: ""
            onRes(pseudo)
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error")
            onRes("")
        }
}