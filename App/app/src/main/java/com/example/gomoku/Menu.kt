package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Menu(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var pp by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }
    if(auth.currentUser != null){
        Log.i("TAG", "Current user: ${auth.currentUser!!.uid}")
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { res ->
                Log.i("TAG", "Profile: ${res.data}")
                pp = res.data!!["profile_pic"].toString()
                elo = res.data!!["elo"].toString().toInt()
            }
            .addOnFailureListener {
                Log.i("TAG", "Profile: Error")
            }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(
                onClick = {
                    navController.navigate(Screens.Friends.name)
                },
                modifier = Modifier.padding(4.dp),
                enabled = auth.currentUser != null
            ) {
                Text(text = stringResource(id = R.string.friends))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    onClick = {
                        navController.navigate(Screens.Leaderboard.name)
                    },
                    modifier = Modifier.padding(4.dp),
                    enabled = auth.currentUser != null
                ) {
                    Text(text = "Elo : $elo")
                }

                Box{
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(4.dp).size(72.dp)
                    ) {
                        if(pp != "" && auth.currentUser != null){
                            val resId = remember(pp) {
                                context.resources.getIdentifier(pp, "drawable", context.packageName)
                            }
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Profile picture",
                                modifier = Modifier.size(72.dp)
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Account",
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Log.i("TAG", "Menu: ${auth.currentUser?.email}")
                        if(auth.currentUser != null){
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.profile)) },
                                onClick = {
                                    expanded = false
                                    recup_moi(auth, db){ pseudo ->
                                        navController.navigate("${Screens.Profile.name}/$pseudo")
                                    }

                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.sign_out)) },
                                onClick = {
                                    expanded = false
                                    auth.signOut()
                                    elo = 0
                                }
                            )
                        }else{
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.sign_in)) },
                                onClick = {
                                    expanded = false
                                    navController.navigate(Screens.Sign_in.name)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.sign_up)) },
                                onClick = {
                                    expanded = false
                                    navController.navigate(Screens.Sign_up.name)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(128.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = {
                    navController.navigate(Screens.Offline_lobby.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(id = R.string.play_offline))
            }
            Button(
                onClick = {
                    navController.navigate(Screens.Online.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f),
                enabled = auth.currentUser != null
            ) {
                Text(text = stringResource(id = R.string.play_online))
            }
            Button(
                onClick = {
                    navController.navigate(Screens.Asynchronus.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f),
                enabled = auth.currentUser != null
            ) {
                Text(text = stringResource(id = R.string.play_asynchronus))
            }
        }
    }
}