package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.nav.Screens
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
    Row(
        modifier = when (i) {
            1 -> Modifier.fillMaxWidth().padding(end = 48.dp)
            2 -> Modifier.fillMaxWidth().padding(start = 48.dp)
            else -> Modifier.fillMaxWidth()
        },
        horizontalArrangement = when(i) {
            1 -> Arrangement.End
            2 -> Arrangement.Start
            else -> Arrangement.Center
        },
        verticalAlignment = Alignment.CenterVertically
    ){
        when (i) {
            1 -> {
                if(timer != "") Custom_card(timer)
                Custom_card(player)
                Canvas(modifier = Modifier.size(30.dp).padding(4.dp)){
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
            }

            2 -> {
                Canvas(modifier = Modifier.size(30.dp).padding(4.dp)){
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
                Custom_card(player)
                if(timer != "") Custom_card(timer)
            }
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

@Composable
fun Recup_request(request : String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = request,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f).padding(start = 8.dp, end = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row{
                IconButton(
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Accepter",
                        tint = Color(0xFF4CAF50)
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Refuser",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

@Composable
fun Recup_friend(friend : String){
    // TODO
}