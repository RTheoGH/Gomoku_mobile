package com.example.gomoku.user

import android.util.Log
import android.util.Patterns
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.R
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Sign_in(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    val context = LocalContext.current

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
                label = { Text(text = stringResource(R.string.pseudo)) },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.mdp)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if(pseudo.isEmpty() || password.isEmpty()){
                        errorMessage = R.string.error_fields.toString()
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
                                        errorMessage = context.getString(R.string.error_pseudo_mdp)
                                    }
                            } else {
                                errorMessage = context.getString(R.string.error_pseudo)
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = context.getString(R.string.error_connexion)
                        }
                },
            ) {
                Text(text = stringResource(R.string.sign_in))
            }
        }
    }
}

@Composable
fun Sign_up(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore){
    val context = LocalContext.current

    var pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm_password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var pseudoError = pseudo.isEmpty()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val short_password_error = password.isNotEmpty() && password.length < 6
    var emailError by remember { mutableStateOf(false) }

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
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.padding(4.dp).size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pseudo,
                onValueChange = { if(it.length <= 16) pseudo = it },
                label = { Text(text = stringResource(R.string.pseudo)) },
                modifier = Modifier.padding(4.dp),
                isError = pseudoError,
                supportingText = {
                    if (pseudoError) {
                        Text(stringResource(R.string.invalid_pseudo), color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                },
                label = { Text(text = stringResource(R.string.mail)) },
                modifier = Modifier.padding(4.dp),
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        Text(stringResource(R.string.invalid_email), color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.mdp)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp),
                isError = short_password_error,
                supportingText = {
                    if (short_password_error) {
                        Text(stringResource(R.string.minimum_mdp_length), color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = confirm_password,
                onValueChange = { confirm_password = it },
                label = { Text(text = stringResource(R.string.confirm_mdp)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(password == confirm_password) {
                        db.collection("users").whereEqualTo("pseudo", pseudo).get().addOnSuccessListener { res ->
                            if (!res.isEmpty) {
                                errorMessage = context.getString(R.string.username_taken)
                            } else {
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authTask ->
                                    if (authTask.isSuccessful) {
                                        Log.i("TAG", "createUserWithEmailAndPassword:success : ${auth.currentUser!!.uid}")
                                        val user = User(
                                            email = email,
                                            pseudo = pseudo,
                                            elo = 1000,
                                            requests = listOf(),
                                            friends = listOf()
                                        )

                                        db.collection("users")
                                            .document(auth.currentUser!!.uid)
                                            .set(user).addOnCompleteListener { dbTask ->
                                                if (dbTask.isSuccessful) {
                                                    Log.i("TAG", "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}")
                                                    //navController.navigate(Screens.Profile.name)
                                                    navController.navigate("${Screens.Profile.name}/${user.pseudo}")
                                                } else {
                                                    Log.i("TAG", "Error adding document", dbTask.exception)
                                                    errorMessage = context.getString(R.string.error_create_profile)
                                                }
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    errorMessage = if (e is FirebaseAuthUserCollisionException) {
                                        context.getString(R.string.email_taken)
                                    } else {
                                        context.getString(R.string.error_create_account)
                                    }
                                }
                            }
                        }
                    } else {
                        errorMessage = context.getString(R.string.mdp_match)
                    }
                },
            ) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }
}