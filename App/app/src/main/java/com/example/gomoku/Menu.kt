package com.example.gomoku

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("DiscouragedApi")
@Composable
fun Menu(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    var pp by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            Log.i("TAG", "Current user: ${auth.currentUser!!.uid}")
            db.collection("users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { res ->
                    Log.i("TAG", "Profile: ${res.data}")
                    pp = res.data!!["profile_pic"].toString()
                    elo = res.data!!["elo"].toString().toInt()
                    loading = false
                }
                .addOnFailureListener {
                    Log.i("TAG", "Profile: Error")
                    loading = false
                }
        } else loading = false
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
                    if(loading) Chargement()
                    else Text(text = "Elo : $elo")
                }

                Box{
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(4.dp).size(72.dp)
                    ) {
                        if(loading){
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }else {
                            if (pp != "" && auth.currentUser != null) {
                                val resId = remember(pp) {
                                    context.resources.getIdentifier(pp,"drawable",context.packageName)
                                }
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = "Profile picture",
                                    modifier = Modifier.size(72.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = "Account",
                                    modifier = Modifier.size(72.dp)
                                )
                            }
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

        Spacer(modifier = Modifier.height(164.dp))

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

            Spacer(modifier = Modifier.height(128.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                TextButton(
                    onClick = { showDialog = true }
                ) {
                    Text(text = stringResource(id = R.string.about))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        (context as? MainActivity)?.requestPermission()
                    }
                ){
                    Text("Notifications")
                }
            }

            if(showDialog){
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    text = {
                        Column{
                            Text(text = "© 2025 Reynier Théo - Viguier Killian")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                buildAnnotatedString {
                                    append("Icon credit : ")
                                    withLink(
                                        LinkAnnotation.Url(
                                            "https://www.flaticon.com/fr/auteurs/anastassiya-motokhova",
                                            TextLinkStyles(style = SpanStyle(color = Color.Blue))
                                        )
                                    ){
                                        append("Anastassiya Motokhova")
                                    }
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "Cool !")
                        }
                    }
                )
            }
        }
    }
}
