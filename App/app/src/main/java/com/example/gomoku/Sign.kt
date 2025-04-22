package com.example.gomoku

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Sign_in(pad : PaddingValues, navController: NavHostController){
    var pseudo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                value = "",
                onValueChange = { pseudo = it },
                label = { Text(text = "Pseudonyme") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { password = it },
                label = { Text(text = "Mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    //TODO
                    navController.navigate(Screens.Menu.name)
                },
            ) {
                Text(text = "Se connecter")
            }
        }
    }

}

@Composable
fun Sign_up(pad : PaddingValues, navController: NavHostController){
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
                    //TODO
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
                value = "",
                onValueChange = { pseudo = it },
                label = { Text(text = "Pseudonyme") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { password = it },
                label = { Text(text = "Mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { confirm_password = it },
                label = { Text(text = "Confirmation du mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    //TODO
                    navController.navigate(Screens.Profile.name)
                },
            ) {
                Text(text = "S'inscrire")
            }
        }
    }
}

@Composable
fun Sign_out(pad : PaddingValues, navController: NavHostController){
    //TODO
}