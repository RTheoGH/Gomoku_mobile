package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Profile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    //TODO

    var pseudo by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }

    var current_user = auth.currentUser!!
    Log.i("TAG", "Profile: ${current_user.uid}")

    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            Log.i("TAG", "Profile: ${res.data}")
            pseudo = res.data!!["pseudo"].toString()
            elo = res.data!!["elo"].toString().toInt()
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error")
        }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Back(navController)

            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.padding(4.dp).size(72.dp)
            )

            IconButton(
                onClick = { navController.navigate(Screens.EditProfile.name) },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Pseudo : $pseudo")
            Text(text = "Elo : $elo")

            //TODO
        }
    }
}

@Composable
fun EditProfile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore) {
    var pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var new_password by remember { mutableStateOf("") }
    var new_confirm_password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var current_user = auth.currentUser!!
    Log.i("TAG", "Profile: ${current_user.uid}")

    LaunchedEffect(current_user.uid) {
        db.collection("users").document(current_user.uid).get()
            .addOnSuccessListener { res ->
                Log.i("TAG", "Profile: ${res.data}")
                pseudo = res.data!!["pseudo"].toString()
            }
            .addOnFailureListener {
                Log.i("TAG", "Profile: Error")
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = {
                    //TODO ?
                },
                modifier = Modifier.padding(4.dp).size(72.dp)
            ){
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    modifier = Modifier.padding(4.dp).size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pseudo,
                onValueChange = { pseudo = it },
                label = { Text(text = "Pseudonyme") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = new_password,
                onValueChange = { new_password = it },
                label = { Text(text = "Nouveau mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = new_confirm_password,
                onValueChange = { new_confirm_password = it },
                label = { Text(text = "Confirmation du mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    //TODO
                    if(new_password == new_confirm_password){
                        db.collection("users").document(current_user.uid).update(
                            "pseudo", pseudo
                        ).addOnSuccessListener {
                            Log.i("TAG", "Profile: Pseudo updated")
                        }.addOnFailureListener {
                            Log.i("TAG", "Profile: Error updating pseudo")
                        }
                        if(new_password.isNotEmpty()){
                            val credential = EmailAuthProvider.getCredential(current_user.email!!, password)
                            current_user.reauthenticate(credential).addOnSuccessListener {
                                current_user.updatePassword(new_password).addOnSuccessListener {
                                    Log.i("TAG", "Profile: Password updated")
                                }.addOnFailureListener {
                                    Log.i("TAG", "Profile: Error updating password")
                                }
                            }
                            .addOnFailureListener {
                                errorMessage = "Mot de passe incorrect"
                            }
                        }
                        navController.navigate(Screens.Profile.name)
                    }else{
                        errorMessage = "Les mots de passe ne correspondent pas"
                    }
                },
            ) {
                Text(text = "Modifier les informations")
            }
        }
    }
}