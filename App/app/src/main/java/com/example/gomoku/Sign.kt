package com.example.gomoku

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Sign_in(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    var pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.padding(4.dp).size(72.dp)
            )

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
                    errorMessage = null
                    if(pseudo.isEmpty() || password.isEmpty()){
                        errorMessage = "Veuillez remplir tous les champs"
                        return@Button
                    }

                    db.collection("users")
                        .whereEqualTo("pseudo", pseudo).get()
                        .addOnSuccessListener { res ->
                            if(!res.isEmpty){
                                val email = res.documents[0].get("email").toString()
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        Log.i("TAG", "signInWithEmailAndPassword:success : ${auth.currentUser!!.uid}")
                                        navController.navigate(Screens.Menu.name)
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Pseudo ou mot de passe incorrect"
                                    }
                            } else {
                                errorMessage = "Pseudonyme introuvable"
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Erreur de connexion"
                        }
                },
            ) {
                Text(text = "Se connecter")
            }
        }
    }
}

@Composable
fun Sign_up(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    var pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm_password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Back(navController)

        Spacer(modifier = Modifier.height(8.dp))

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
            ) {
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
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
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
                value = confirm_password,
                onValueChange = { confirm_password = it },
                label = { Text(text = "Confirmation du mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    //TODO
                    if(password == confirm_password){
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    Log.i("TAG", "createUserWithEmailAndPassword:success : ${auth.currentUser!!.uid}")
                                    val user = User(
                                        email = email,
                                        pseudo = pseudo,
                                        elo = 1000
                                    )

                                    db.collection("users").document(auth.currentUser!!.uid)
                                        .set(user).addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Log.i("TAG", "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}")
                                                navController.navigate(Screens.Profile.name)
                                            } else {
                                                Log.i("TAG", "Error adding document", dbTask.exception)
                                            }
                                        }
                                }
                            }
                    }else{
                        //TODO
                    }
                },
            ) {
                Text(text = "S'inscrire")
            }
        }
    }
}