package com.example.gomoku.user

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Custom_card
import com.example.gomoku.nav.Screens
import com.example.gomoku.recup_moi
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Profile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, p: String?){
    //TODO : Meilleur visuel ?

    var pseudo by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }

    var isMe by remember { mutableStateOf(false) }
    recup_moi(auth,db){
        isMe = it == p
    }

    db.collection("users").whereEqualTo("pseudo", p).get()
        .addOnSuccessListener { res ->
            pseudo = res.documents.first().data!!["pseudo"].toString()
            elo = res.documents.first().data!!["elo"].toString().toInt()
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

            if(isMe) {
                IconButton(
                    onClick = { navController.navigate(Screens.EditProfile.name) },
                    modifier = Modifier.padding(4.dp).size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.padding(4.dp).size(32.dp)
                    )
                }
            }else{
                Spacer(modifier = Modifier.padding(4.dp).width(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Pseudonyme")
            Custom_card(pseudo)
            Text(text = "Elo")
            Custom_card(elo.toString())


            Spacer(modifier = Modifier.height(32.dp))

            //TODO : Afficher les parties jouées
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {  }
        }
    }
}

@Composable
fun EditProfile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current

    var pseudo by remember { mutableStateOf("") }
    var current_pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var new_password by remember { mutableStateOf("") }
    var new_confirm_password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val short_password_error = password.isNotEmpty() && password.length < 6

    var pseudo_edit by remember { mutableStateOf(false) }
    var password_edit by remember { mutableStateOf(false) }

    var current_user = auth.currentUser!!
    Log.i("TAG", "Profile: ${current_user.uid}")

    LaunchedEffect(current_user.uid) {
        db.collection("users").document(current_user.uid).get()
            .addOnSuccessListener { res ->
                Log.i("TAG", "Profile: ${res.data}")
                pseudo = res.data!!["pseudo"].toString()
                current_pseudo = pseudo
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
                onValueChange = { if(it.length <= 16) pseudo = it },
                label = { Text(text = "Pseudonyme") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp),
                isError = short_password_error,
                supportingText = {
                    if (short_password_error) {
                        Text("Le mot de passe doit contenir au moins 6 caractères")
                    }
                }
            )

            OutlinedTextField(
                value = new_password,
                onValueChange = { new_password = it },
                label = { Text(text = "Nouveau mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp),
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
                    if(new_password == new_confirm_password){
                        var pseudo_edit_done = false
                        var password_edit_done = false

                        val modifs = {
                            if(pseudo_edit && password_edit){
                                Toast.makeText(context, "Pseudo et mot de passe modifiés", Toast.LENGTH_SHORT).show()
                            }else if(pseudo_edit){
                                Toast.makeText(context, "Pseudo modifié", Toast.LENGTH_SHORT).show()
                            }else if(password_edit){
                                Toast.makeText(context, "Mot de passe modifié", Toast.LENGTH_SHORT).show()
                            }
                            navController.navigate(Screens.Profile.name)
                        }

                        db.collection("users").whereEqualTo("pseudo", pseudo).get().addOnSuccessListener { res ->
                            val is_pseudo_taken = res.documents.any { it.id != current_user.uid }
                            if (is_pseudo_taken) {
                                errorMessage = "Pseudonyme déjà utilisé"
                            }else{
                                if(pseudo != current_pseudo){
                                    db.collection("users").document(current_user.uid).update(
                                        "pseudo", pseudo
                                    ).addOnSuccessListener {
                                        Log.i("TAG", "Profile: Pseudo updated")
                                        pseudo_edit = true
                                        pseudo_edit_done = true
                                        if(password_edit_done || new_password.isEmpty()) modifs()
                                    }.addOnFailureListener {
                                        Log.i("TAG", "Profile: Error updating pseudo")
                                    }
                                }else{
                                    pseudo_edit_done = true
                                    if(password_edit_done || new_password.isEmpty()) modifs()
                                }
                                if(new_password.isNotEmpty()){
                                    val credential = EmailAuthProvider.getCredential(current_user.email!!, password)
                                    current_user.reauthenticate(credential).addOnSuccessListener {
                                        current_user.updatePassword(new_password).addOnSuccessListener {
                                            Log.i("TAG", "Profile: Password updated")
                                            password_edit = true
                                            password_edit_done = true
                                            if(pseudo_edit_done) modifs()
                                        }.addOnFailureListener {
                                            Log.i("TAG", "Profile: Error updating password")
                                            errorMessage = "Erreur lors de la modification du mot de passe"
                                        }
                                    }
                                        .addOnFailureListener {
                                            errorMessage = "Mot de passe incorrect"
                                        }
                                }else{
                                    password_edit_done = true
                                    if(pseudo_edit_done) modifs()
                                }
                            }
                        }
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