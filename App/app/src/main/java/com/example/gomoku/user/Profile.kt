package com.example.gomoku.user

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Custom_card
import com.example.gomoku.R
import com.example.gomoku.nav.Screens
import com.example.gomoku.recup_moi
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("DiscouragedApi")
@Composable
fun Profile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore, p: String?) {
    //TODO : Meilleur visuel ?
    val context = LocalContext.current

    var erreur by remember { mutableStateOf("") }

    var pseudo by remember { mutableStateOf("") }
    var pp by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }
    var friends by remember { mutableStateOf(listOf<String>()) }

    var isMe by remember { mutableStateOf(false) }
    var isFriend by remember { mutableStateOf(false) }

    // timestamp et +/- elo_change
    var match_history = remember { mutableStateMapOf<String, String>() }

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val drawableOptions = listOf(
        "chat",
        "heureux",
        "mordu",
        "ohmondieu",
        "panda",
        "ressentiment"
    )

    recup_moi(auth, db) {
        isMe = it == p
    }

    LaunchedEffect(p) {
        db.collection("users").whereEqualTo("pseudo", p).get()
            .addOnSuccessListener { res ->
                pseudo = res.documents.first().data!!["pseudo"].toString()
                pp = res.documents.first().data!!["profile_pic"].toString()
                elo = res.documents.first().data!!["elo"].toString().toInt()
            }
            .addOnFailureListener {
                Log.i("TAG", "Profile: Error")
            }

        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { res ->
                friends = res.data!!["friends"] as List<String>
                isFriend = friends.contains(p)
            }
            .addOnFailureListener {
                Log.i("TAG", "Profile: Error getting own friends")
            }


        db.collection("matches").whereArrayContains("players", p!!).get()
            .addOnSuccessListener { res ->
                res.documents.forEach { doc ->
                    val data = doc.data ?: return@forEach

                    val timestamp = data["timestamp"]
                    val winner = data["winner"]
                    val elo_change = (data["elo_change"] as? Long)?.toInt() ?: 0

                    //Log.i("TAG", "Profile_matches: $timestamp $winner $elo_change")
                    Log.i("TAG", "$elo_change")

                    val string_elo = if (winner == p) "+${elo_change}" else "-${elo_change}"
                    val formattedDate = if (timestamp is Timestamp) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        sdf.format(timestamp.toDate())
                    } else {
                        timestamp.toString()
                    }
                    match_history[formattedDate] = string_elo
                }
            }.addOnFailureListener {
                Log.i("TAG", "Profile: Error getting match history")
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Back(navController)

            val resId = remember(pp) {
                context.resources.getIdentifier(pp, "drawable", context.packageName)
            }

            if (isMe) {
                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(4.dp).size(72.dp)
                    ) {
                        if (resId != 0) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Profile picture",
                                modifier = Modifier.clip(CircleShape).padding(4.dp).size(72.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Account",
                                modifier = Modifier.padding(4.dp).size(72.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        drawableOptions.forEach { imageName ->
                            val option = context.resources.getIdentifier(
                                imageName,
                                "drawable",
                                context.packageName
                            )
                            DropdownMenuItem(
                                onClick = {
                                    pp = imageName
                                    expanded = false

                                    val user = auth.currentUser!!
                                    db.collection("users").document(user.uid).update("profile_pic", imageName)
                                        .addOnSuccessListener { Log.i("TAG", "Profile: Profile picture updated") }
                                        .addOnFailureListener { Log.i("TAG", "Profile: Error updating profile picture") }
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = option),
                                            contentDescription = "Profile picture",
                                            modifier = Modifier.size(32.dp).clip(CircleShape)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Profile picture",
                        modifier = Modifier.clip(CircleShape).padding(4.dp).size(72.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier.padding(4.dp).size(72.dp)
                    )
                }
            }

            if (isMe) {
                IconButton(
                    onClick = { navController.navigate(Screens.EditProfile.name) },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(32.dp)
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.pseudo))
            Custom_card(pseudo)
            Text(text = "Elo")
            Custom_card(elo.toString())

            if (!isMe && !isFriend) {
                IconButton(
                    onClick = {
                        val current_user = auth.currentUser!!

                        db.collection("users").document(current_user.uid).get()
                            .addOnSuccessListener { res ->
                                val current_p = res.data!!["pseudo"].toString()

                                db.collection("users").whereEqualTo("pseudo", p).get()
                                    .addOnSuccessListener { res2 ->
                                        if (res2.documents.isNotEmpty()) {
                                            val target = res2.documents.first().id

                                            db.collection("users")
                                                .document(target)
                                                .update(
                                                    "requests",
                                                    FieldValue.arrayUnion(current_p)
                                                )
                                                .addOnSuccessListener {
                                                    Log.i("TAG", "Profile: Request sent")
                                                }
                                                .addOnFailureListener {
                                                    Log.i("TAG", "Profile: Error sending request")

                                                }
                                        } else {
                                            Log.i("TAG", "Profile: User not found")
                                            erreur = context.getString(R.string.user_not_found)
                                        }

                                    }
                            }
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Add",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(32.dp)
                    )
                }
            }else{
                Spacer(modifier = Modifier
                    .padding(4.dp)
                    .width(32.dp))
            }

            Text(text = erreur)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Historique des parties")

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(match_history.toList()) { (date, elo) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = date)
                        Text(
                            text = elo,
                            color = if (elo.startsWith("+")) Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun EditProfile(pad : PaddingValues, navController: NavHostController, auth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current

    var pseudo by remember { mutableStateOf("") }
    var pp by remember { mutableStateOf("") }
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
                pp = res.data!!["profile_pic"].toString()
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
            val resId = remember(pp){
                context.resources.getIdentifier(pp, "drawable", context.packageName)
            }

            if(pp != ""){
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(4.dp)
                        .size(72.dp)
                )
            }else{
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pseudo,
                onValueChange = { if(it.length <= 16) pseudo = it },
                label = { Text(text = stringResource(R.string.pseudo)) },
                modifier = Modifier.padding(4.dp)
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
                value = new_password,
                onValueChange = { new_password = it },
                label = { Text(text = stringResource(R.string.new_mdp)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(4.dp),
            )

            OutlinedTextField(
                value = new_confirm_password,
                onValueChange = { new_confirm_password = it },
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
                    if(new_password == new_confirm_password){
                        var pseudo_edit_done = false
                        var password_edit_done = false

                        val modifs = {
                            if(pseudo_edit && password_edit){
                                Toast.makeText(context, context.getString(R.string.edit_pm), Toast.LENGTH_SHORT).show()
                            }else if(pseudo_edit){
                                Toast.makeText(context, context.getString(R.string.edit_p), Toast.LENGTH_SHORT).show()
                            }else if(password_edit){
                                Toast.makeText(context, context.getString(R.string.edit_m), Toast.LENGTH_SHORT).show()
                            }
                            navController.navigate(Screens.Profile.name) {
                                popUpTo(Screens.EditProfile.name) { inclusive = true }
                            }
                        }

                        db.collection("users").whereEqualTo("pseudo", pseudo).get().addOnSuccessListener { res ->
                            val is_pseudo_taken = res.documents.any { it.id != current_user.uid }
                            if (is_pseudo_taken) {
                                errorMessage = context.getString(R.string.username_taken)
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
                                            errorMessage = context.getString(R.string.error_update_mdp)
                                        }
                                    }
                                        .addOnFailureListener {
                                            errorMessage = context.getString(R.string.incorrect_mdp)
                                        }
                                }else{
                                    password_edit_done = true
                                    if(pseudo_edit_done) modifs()
                                }
                            }
                        }
                    }else{
                        errorMessage = context.getString(R.string.mdp_match)
                    }
                },
            ) {
                Text(text = stringResource(R.string.edit_infos))
            }
        }
    }
}