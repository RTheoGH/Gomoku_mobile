package com.example.gomoku.game

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Chargement
import com.example.gomoku.Custom_card
import com.example.gomoku.Custom_row
import com.example.gomoku.ModeText
import com.example.gomoku.R
import com.example.gomoku.SecondaryText
import com.example.gomoku.formatSecondsToTime
import com.example.gomoku.nav.Screens
import com.example.gomoku.switchWithIcon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

@Composable
fun Online(pad : PaddingValues, navController: NavHostController){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)){
        Back(navController)

        Spacer(modifier = Modifier.height(112.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            ModeText("Online")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screens.Online_matchmaking.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ){
                Text(text = "Matchmaking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screens.Online_create.name)
                },
                modifier = Modifier.padding(2.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(R.string.online_create))
            }
            Button(
                onClick = {
                    navController.navigate(Screens.Online_join.name)
                },
                modifier = Modifier.padding(2.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(R.string.online_join))
            }
        }
    }
}

@Composable
fun Online_matchmaking(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase
) {
    var isLoading by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var tempsEcoule by remember { mutableStateOf(0) }

    var ranked by remember { mutableStateOf(false) }
    var blitz by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var elo by remember { mutableStateOf(0) }
    var profile_pic by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        val currentUid = auth.currentUser!!.uid
        db.collection("users").document(currentUid).get()
            .addOnSuccessListener {
                username = it.get("pseudo").toString()
                elo = it.get("elo").toString().toInt()
                profile_pic = it.get("profile_pic").toString()
            }
            .addOnFailureListener {
                Log.i("TAG", "Online_matchmaking: Error getting user data")
            }
    }

    DisposableEffect(Unit){
        onDispose {
            val currentUid = auth.currentUser!!.uid
            rdb.getReference("matchmaking").child(currentUid).removeValue()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)){
        Back(navController)

        Spacer(modifier = Modifier.height(96.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ModeText("Online")
            Spacer(modifier = Modifier.height(8.dp))

            SecondaryText("Matchmaking")

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().height(1.dp).border(1.dp,color = MaterialTheme.colorScheme.primary)){}
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryText(stringResource(R.string.pseudo))
                SecondaryText("Elo")
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Custom_card(username)
                Custom_card(elo.toString())
            }

            Row(verticalAlignment = Alignment.CenterVertically){
                SecondaryText(stringResource(R.string.ranked))
                ranked = switchWithIcon()
                Log.i("TAG", "Ranked: $ranked")
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryText("Blitz")
                blitz = switchWithIcon()
                Log.i("TAG", "Blitz: $blitz")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().height(1.dp).border(1.dp,color = MaterialTheme.colorScheme.primary)){}
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        isSearching = false
                        isLoading = false
                        val currentUid = auth.currentUser!!.uid
                        rdb.getReference("matchmaking").child(currentUid).removeValue()
                    },
                    enabled = isSearching
                ){
                    Text(text = stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        isLoading = true
                        isSearching = true
                    },
                    enabled = !isSearching
                ) {
                    Text(text = stringResource(R.string.start))
                }
            }

            if(isLoading){
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Chargement()
                    Spacer(modifier = Modifier.width(4.dp))
                    Custom_card(formatSecondsToTime(tempsEcoule))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if(tempsEcoule < 20) stringResource(R.string.search20)
                        else if (tempsEcoule < 40) stringResource(R.string.search40)
                        else if (tempsEcoule < 60) stringResource(R.string.search60)
                        else stringResource(R.string.searchmore)
                    )
                }
            }
        }
    }

    LaunchedEffect(isSearching) {
        if(!isSearching){
            tempsEcoule = 0
            return@LaunchedEffect
        }

        val currentUid = auth.currentUser!!.uid

        while (isSearching) {
            delay(1000)

            val matchedLobby = rdb.getReference("matchmaking").child(currentUid)
                .child("matched_lobby_id").get().await()?.getValue(String::class.java)

            if(matchedLobby != null){
                rdb.getReference("matchmaking").child(currentUid).removeValue()
                isSearching = false
                isLoading = false
                navController.navigate("${Screens.Online_game.name}/$matchedLobby")
                break
            }

            val found = searchMatch(
                currentUid, username, elo, profile_pic,
                ranked, blitz,
                tempsEcoule,
                rdb, navController
            )
            if (found) {
                isSearching = false
                isLoading = false
                break
            }
            tempsEcoule += 1
        }
    }
}

@Composable
fun Online_create(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase
){
    val context = LocalContext.current
    var lobby_name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var ranked by remember { mutableStateOf(false) }
    var blitz by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Spacer(modifier = Modifier.height(112.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            ModeText("Online")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lobby_name,
                onValueChange = { if(it.length <= 20) lobby_name = it },
                label = { Text(text = stringResource(R.string.online_name)) },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if(it.length <= 10) password = it },
                label = { Text(text = stringResource(R.string.mdp)) },
                modifier = Modifier.padding(4.dp),
                visualTransformation = PasswordVisualTransformation(),
            )

            Row(verticalAlignment = Alignment.CenterVertically){
                SecondaryText(stringResource(R.string.ranked))
                ranked = switchWithIcon()
                Log.i("TAG", "Ranked: $ranked")
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryText("Blitz")
                blitz = switchWithIcon()
                Log.i("TAG", "Blitz: $blitz")
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val lobbyId = lobby_name.trim()
                    val uid = auth.currentUser!!.uid
                    var uid_name = ""
                    var pp = ""

                    if(lobbyId.isNotEmpty()){
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener {
                                uid_name = it.get("pseudo").toString()
                                pp = it.get("profile_pic").toString()

                                Log.i("TAG", "Online_create: $uid_name")

                                val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
                                Log.i("TAG", "Online_create: $lobbyRef")

                                val board = List(15) { i ->
                                    List(15) { j ->
                                        gomokuCellToMap(GomokuCell(i, j, CellState.EMPTY))
                                    }
                                }

                                val player1Data = mapOf(
                                    "uid" to uid,
                                    "pseudo" to uid_name,
                                    "profile_pic" to pp,
                                    "timer" to 300
                                )

                                val lobbyData = mapOf(
                                    "host" to uid_name,
                                    "password" to password.trim(),
                                    "player1" to player1Data,
                                    "player2" to "",
                                    "winner" to "",
                                    "status" to "waiting",
                                    "ranked" to ranked,
                                    "blitz" to blitz,
                                    "created_at" to System.currentTimeMillis(),
                                    "board" to board,
                                    "turn" to 0,
                                    "turn_history" to emptyList<String>(),
                                    "chat" to emptyList<String>()
                                )
                                Log.i("TAG", "Online_create: $lobbyData")

                                lobbyRef.setValue(lobbyData).addOnSuccessListener {
                                    navController.navigate(Screens.Online_lobby.name + "/$lobbyId")
                                    Log.i("TAG", "Online_create: redirection")
                                }.addOnFailureListener {
                                    // Lobby deja existant
                                    errorMessage = context.getString(R.string.online_error_existing_lobby)
                                }
                            }.addOnFailureListener {
                                errorMessage = context.getString(R.string.online_error_dataroom_get)
11                            }
                    }
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(R.string.online_create))
            }
        }
    }
}

@Composable
fun Online_join(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase
){
    val context = LocalContext.current

    var lobby_name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Spacer(modifier = Modifier.height(112.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ModeText("Online")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lobby_name,
                onValueChange = { if(it.length <= 20) lobby_name = it },
                label = { Text(text = stringResource(R.string.online_name)) },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if(it.length <= 10) password = it },
                label = { Text(text = stringResource(R.string.mdp)) },
                modifier = Modifier.padding(4.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val lobbyId = lobby_name.trim()
                    val enteredPassword = password.trim()
                    val uid = auth.currentUser!!.uid
                    var uid_name = ""
                    var pp = ""

                    if(lobbyId.isEmpty() || uid == ""){
                        errorMessage = context.getString(R.string.error_fields)
                        return@Button
                    }

                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            uid_name = userDoc.get("pseudo").toString()
                            pp = userDoc.get("profile_pic").toString()

                            val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
                            lobbyRef.get().addOnSuccessListener { snapshot ->
                                if (!snapshot.exists()) {
                                    errorMessage = context.getString(R.string.online_not_found)
                                    return@addOnSuccessListener
                                }

                                val lobbyPassword = snapshot.child("password").getValue(String::class.java)
                                val currentPlayer1 = snapshot.child("player1").child("pseudo").getValue(String::class.java)
                                val currentPlayer2 = snapshot.child("player2").child("pseudo").getValue(String::class.java)

                                when{
                                    lobbyPassword != enteredPassword -> errorMessage = context.getString(R.string.incorrect_mdp)
                                    currentPlayer1 == uid_name || currentPlayer2 == uid_name -> errorMessage = context.getString(R.string.online_already_in)
                                    !currentPlayer2.isNullOrEmpty() -> errorMessage = context.getString(R.string.online_full)
                                    else -> {
                                        val player2Data = mapOf(
                                            "uid" to uid,
                                            "pseudo" to uid_name,
                                            "profile_pic" to pp,
                                            "timer" to 300
                                        )

                                        lobbyRef.child("player2").setValue(player2Data)
                                            .addOnSuccessListener {
                                                navController.navigate(Screens.Online_lobby.name + "/$lobbyId")
                                            }
                                            .addOnFailureListener {
                                                errorMessage = context.getString(R.string.online_error_join)
                                            }
                                    }
                                }
                            }.addOnFailureListener {
                                errorMessage = context.getString(R.string.online_error_access_db)
                            }
                        }
                }
            ){
                Text(text = stringResource(R.string.online_join))
            }
        }
    }

}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Online_lobby(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase,
    lobbyId: String
){
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    var player1 by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("waiting") }
    var ranked by remember { mutableStateOf(false) }
    var blitz by remember { mutableStateOf(false) }
    var currentUidName by remember { mutableStateOf("") }

    val isHost by derivedStateOf { player1 == currentUidName }
    val canStart by derivedStateOf { player2.isNotEmpty() && isHost && status == "ready" }

    val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
    val valueEventListener = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            player1 = snapshot.child("player1").child("pseudo").getValue(String::class.java) ?: ""
            player2 = snapshot.child("player2").child("pseudo").getValue(String::class.java) ?: ""
            password = snapshot.child("password").getValue(String::class.java) ?: ""
            status = snapshot.child("status").getValue(String::class.java) ?: "waiting"
            ranked = snapshot.child("ranked").getValue(Boolean::class.java) ?: false
            blitz = snapshot.child("blitz").getValue(Boolean::class.java) ?: false

            if(status == "started"){
                navController.navigate("${Screens.Online_game.name}/$lobbyId")
            }else if (player2.isNotEmpty()){
                lobbyRef.child("status").setValue("ready")
            }

            if(status == "deleted" && !isHost){
                showDialog = true
            }

            Log.i("TAG", "Online_lobby: datachanged : $lobbyRef $player1 $player2 $password $status")
        }
        override fun onCancelled(error: DatabaseError) {
            errorMessage = error.message
        }
    }

    DisposableEffect(Unit){
        lobbyRef.addValueEventListener(valueEventListener)
        onDispose {
            lobbyRef.removeEventListener(valueEventListener)
        }
    }

    LaunchedEffect(Unit){
        val currentUid = auth.currentUser!!.uid
        db.collection("users").document(currentUid).get()
            .addOnSuccessListener {
                currentUidName = it.get("pseudo").toString()
            }
    }

    if(showDialog){
        AlertDialog(
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.warning)) },
            text = { Text(text = stringResource(R.string.host_leave)) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.popBackStack(Screens.Menu.name,inclusive = false)
                }) {
                    Text(text = "OK")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {

        Spacer(modifier = Modifier.height(96.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ModeText("Online")

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().height(1.dp).border(1.dp,color = MaterialTheme.colorScheme.primary)){}
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SecondaryText(stringResource(R.string.room))
                SecondaryText(stringResource(R.string.ranked) + "/Blitz")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Custom_card(lobbyId)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (ranked) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                        contentDescription = "Ranked"
                    )
                    SecondaryText("/")
                    Icon(
                        imageVector = if (blitz) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                        contentDescription = "Blitz"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                SecondaryText(stringResource(R.string.mdp))
                SecondaryText("Status")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Custom_card(password)
                Custom_card(status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().height(1.dp).border(1.dp,color = MaterialTheme.colorScheme.primary)){}
            Spacer(modifier = Modifier.height(8.dp))

            SecondaryText(stringResource(R.string.players))

            if(player2.isEmpty()){
                Custom_card(player1)
            }else {
                Row(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Custom_card(player1)
                    Custom_card(player2)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().height(1.dp).border(1.dp,color = MaterialTheme.colorScheme.primary)){}
            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.55f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    onClick = {
                        if(isHost){
                            lobbyRef.child("status").setValue("deleted")
                            lobbyRef.removeValue()
                                .addOnCompleteListener {
                                    Log.i("TAG", "Online_lobby: partie supprimée")
                                    navController.popBackStack(Screens.Menu.name,inclusive = false)
                                }
                        }else if(currentUidName == player2){
                            lobbyRef.child("player2").removeValue()
                                .addOnCompleteListener {
                                    Log.i("TAG", "Online_lobby: joueur 2 supprimé")
                                    navController.popBackStack(Screens.Menu.name,inclusive = false)
                                }
                        }
                    }
                ){
                    Text(text = stringResource(R.string.leave))
                }
                Button(
                    onClick = {
                        lobbyRef.child("status").setValue("started")
                    },
                    enabled = canStart
                ) {
                    Text(text = stringResource(R.string.play))
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Online_game(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase,
    lobbyId: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showDialogWin by remember { mutableStateOf(false) }
    var showDialogLeave by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var transactionDone by remember { mutableStateOf(false) }

    var player1 by remember { mutableStateOf("") }
    var player1uid by remember { mutableStateOf("") }
    var player1pp by remember { mutableStateOf("") }
    var player1timer by remember { mutableIntStateOf(300) }
    var player2 by remember { mutableStateOf("") }
    var player2uid by remember { mutableStateOf("") }
    var player2pp by remember { mutableStateOf("") }
    var player2timer by remember { mutableIntStateOf(300) }

    var ranked by remember { mutableStateOf(false) }
    var blitz by remember { mutableStateOf(false) }

    var winner by remember { mutableStateOf("") }

    var board by remember {
        mutableStateOf(
            MutableList(15) { i ->
                MutableList(15) { j ->
                    GomokuCell(i, j, CellState.EMPTY)
                }
            }
        )
    }

    var playerTurn by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var turn_history = remember { mutableStateListOf(context.getString(R.string.game_start_message)) }
    val listState = rememberLazyListState()
    LaunchedEffect(turn_history.size) {
        listState.animateScrollToItem(turn_history.size)
    }

    var chat = remember { mutableStateListOf<String>() }
    val message = remember { mutableStateOf("") }

    val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
    val valueEventListener = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            player1 = snapshot.child("player1").child("pseudo").getValue(String::class.java) ?: ""
            player1uid = snapshot.child("player1").child("uid").getValue(String::class.java) ?: ""
            player1pp = snapshot.child("player1").child("profile_pic").getValue(String::class.java) ?: ""
            player1timer = snapshot.child("player1").child("timer").getValue(Int::class.java) ?: 300
            player2 = snapshot.child("player2").child("pseudo").getValue(String::class.java) ?: ""
            player2uid = snapshot.child("player2").child("uid").getValue(String::class.java) ?: ""
            player2pp = snapshot.child("player2").child("profile_pic").getValue(String::class.java) ?: ""
            player2timer = snapshot.child("player2").child("timer").getValue(Int::class.java) ?: 300

            ranked = snapshot.child("ranked").getValue(Boolean::class.java) ?: false
            blitz = snapshot.child("blitz").getValue(Boolean::class.java) ?: false

            val boardSnapshot = snapshot.child("board")
            val newBoard = MutableList(15) { MutableList(15) { GomokuCell(0, 0, CellState.EMPTY) } }

            for (i in 0 until boardSnapshot.childrenCount.toInt()) {
                val rowSnapshot = boardSnapshot.child(i.toString())
                for (j in 0 until rowSnapshot.childrenCount.toInt()) {
                    val cellMap = rowSnapshot.child(j.toString()).value as? Map<String, Any> ?: continue
                    newBoard[i][j] = mapToGomokuCell(cellMap)
                }
            }
            board = newBoard
            playerTurn = snapshot.child("turn").getValue(Int::class.java) ?: 0

            val turnHistorySnapshot = snapshot.child("turn_history")
            turn_history.clear()
            for (i in 0 until turnHistorySnapshot.childrenCount.toInt()) {
                turn_history.add(turnHistorySnapshot.child(i.toString()).value.toString())
            }

            isFinished = snapshot.child("status").getValue(String::class.java) == "finished"
            winner = snapshot.child("winner").getValue(String::class.java) ?: ""

            if(winner != "" && isFinished && !transactionDone) {
                showDialogWin = true
                transactionDone = true

                val currentUid = auth.currentUser!!.uid
                val isWinner = currentUid == player1uid && winner == player1 || currentUid == player2uid && winner == player2

                if (ranked) {
                    val myRef = db.collection("users").document(currentUid)
                    var eloChange = 0

                    db.runTransaction { transaction ->

                        val snapshotT = transaction.get(myRef)
                        val myElo = snapshotT.getLong("elo") ?: 0

                        val k = 32

                        val expectedScore = if (isWinner) 1.0 else 0.0
                        val opponentUid = if (currentUid == player1uid) player2uid else player1uid
                        val opponentRef = db.collection("users").document(opponentUid)
                        val opponentSnapshot = transaction.get(opponentRef)
                        val opponentElo = opponentSnapshot.getLong("elo") ?: 0

                        val score = 1.0 / (1.0 + Math.pow(10.0, (opponentElo - myElo).toDouble() / 400.0))
                        eloChange = (k * (expectedScore - score)).toInt()

                        Log.i("TAG", "Elo change: $eloChange")

                        transaction.update(myRef, "elo", myElo + eloChange)

                        null
                    }.addOnSuccessListener {
                        Log.i("TAG", "Elo updated")

                        val matchData = mapOf(
                            "player1" to player1,
                            "player2" to player2,
                            "players" to listOf(player1, player2),
                            "winner" to winner,
                            "elo_change" to abs(eloChange),
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        db.collection("matches").add(matchData)

                        if (isWinner) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                lobbyRef.removeValue()
                                    .addOnSuccessListener { Log.i("TAG", "Partie supprimée") }
                                    .addOnFailureListener { Log.i("TAG", "Partie non supprimée") }
                            }, 10000)
                        }

                    }.addOnFailureListener { Log.i("TAG", "Elo update failed") }
                }else{
                    val matchData = mapOf(
                        "player1" to player1,
                        "player2" to player2,
                        "players" to listOf(player1, player2),
                        "winner" to winner,
                        "elo_change" to 0,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("matches").add(matchData)

                    if (isWinner) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            lobbyRef.removeValue()
                                .addOnSuccessListener { Log.i("TAG", "Partie supprimée") }
                                .addOnFailureListener { Log.i("TAG", "Partie non supprimée") }
                        }, 10000)
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            errorMessage = error.message
        }
    }

    val chatEventListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val m = snapshot.value.toString()
            chat.add(m)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val m = snapshot.value.toString()
            chat.remove(m)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {
            errorMessage = error.message
        }
    }

    DisposableEffect(Unit){
        lobbyRef.addValueEventListener(valueEventListener)
        lobbyRef.child("chat").addChildEventListener(chatEventListener)
        onDispose {
            lobbyRef.removeEventListener(valueEventListener)
            lobbyRef.child("chat").removeEventListener(chatEventListener)
        }
    }

    if(blitz) {
        val currentUid = auth.currentUser!!.uid
        val isMyTurn = (playerTurn == 0 && currentUid == player1uid) || (playerTurn == 1 && currentUid == player2uid)

        LaunchedEffect(isMyTurn, isFinished) {
            while (isMyTurn && !isFinished) {
                delay(1000)
                if (playerTurn == 0) {
                    player1timer--
                    lobbyRef.child("player1").child("timer").setValue(player1timer)
                    if (player1timer <= 0) {
                        showDialogWin = true
                        winner = player2
                        lobbyRef.child("status").setValue("finished")
                        lobbyRef.child("winner").setValue(winner)
                    }
                } else {
                    player2timer--
                    lobbyRef.child("player2").child("timer").setValue(player2timer)
                    if (player2timer <= 0) {
                        showDialogWin = true
                        winner = player1
                        lobbyRef.child("status").setValue("finished")
                        lobbyRef.child("winner").setValue(winner)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(pad)
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(
            onClick = { showDialogLeave = true },
            modifier = Modifier.padding(4.dp).size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(4.dp).size(32.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            if(blitz) Custom_row(1,formatSecondsToTime(player2timer),player2,player2pp)
            else Custom_row(1,"",player2,player2pp)
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Board(
                board = board,
                playerTurn = playerTurn,
                onCellClick = { x, y ->
                    val currentUid = auth.currentUser!!.uid
                    val expectedUid = if (playerTurn == 0) player1uid else player2uid

                    if (currentUid != expectedUid) return@Board

                    if (board[x][y].state == CellState.EMPTY && !isFinished) {
                        val newState = if (playerTurn == 0) CellState.WHITE else CellState.BLACK
                        board[x] = board[x].toMutableList().apply {
                            this[y] = board[x][y].copy(state = newState)
                        }
                        val player = if(playerTurn == 0) player1 else player2
                        val pos_x = x+1
                        val pos_y = y+1

                        turn_history.add(player+" "+context.getString(R.string.played_in)+" "+pos_x+","+pos_y+".")
                        println(turn_history)
                        lobbyRef.child("turn_history").setValue(turn_history)

                        if (check_win(board, x, y, 15)) {
                            showDialogWin = true
                            winner = if(playerTurn == 0) player1 else player2
                            lobbyRef.child("status").setValue("finished")
                            lobbyRef.child("winner").setValue(winner)
                        }

                        playerTurn = 1 - playerTurn
                        lobbyRef.child("turn").setValue(playerTurn)
                        lobbyRef.child("board").setValue(boardToFirebaseFormat(board))
                    }
                }
            )

            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            if(blitz) Custom_row(2,formatSecondsToTime(player1timer),player1,player1pp)
            else Custom_row(2,"",player1,player1pp)

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(80.dp)
            ) {
                items(turn_history){ turn ->
                    Text(modifier = Modifier.padding(horizontal = 5.dp), text = turn)
                }
                if (isFinished){
                    item {
                        val player = if(blitz){
                            if(player1timer <= 0) player2
                            else if(player2timer <= 0) player1
                            else winner
                        }else {
                            if (playerTurn == 0) player1 else player2
                        }
                        Text(modifier = Modifier.padding(horizontal = 5.dp), text = player+" "+ stringResource(R.string.win))
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                items(chat){ message ->
                    Text(modifier = Modifier.padding(horizontal = 5.dp), text = message)
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                TextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    modifier = Modifier.weight(1f).fillMaxWidth(fraction = 0.8f)
                )
                IconButton(
                    onClick = {
                        val sender = if(auth.currentUser!!.uid == player1uid) player1 else player2
                        val newMessage = "$sender : ${message.value}"

                        lobbyRef.child("chat").push().setValue(newMessage)
                        message.value = ""
                    }
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }

            if(showDialogWin){
                AlertDialog(
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    onDismissRequest = { showDialogWin = false },
                    title = { Text(text = stringResource(R.string.game_over)) },
                    text = { Text(text = winner+" "+stringResource(R.string.win)) },
                    confirmButton = {},
                    dismissButton = {
                        Button(onClick = {
                            showDialogWin = false
                            navController.navigate(Screens.Menu.name){
                                popUpTo(Screens.Menu.name){
                                    inclusive = true
                                }
                            }
                        }) {
                            Text(text = stringResource(R.string.leave))
                        }
                    }
                )
            }
        }

        if(showDialogLeave){
            AlertDialog(
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                onDismissRequest = { showDialogLeave = false },
                title = { Text(text = stringResource(R.string.leave)+" ?") },
                text = { Text(text = stringResource(R.string.ask_leave)) },
                confirmButton = {
                    Button(onClick = {
                        showDialogLeave = false
                        val currentUid = auth.currentUser!!.uid

                        val winnerPseudo = if(currentUid == player1uid) player2 else player1
                        val leaverPseudo = if(currentUid == player1uid) player1 else player2
                        val leaverMessage = "$leaverPseudo "+context.getString(R.string.leave_message)

                        lobbyRef.child("status").setValue("finished")
                        lobbyRef.child("winner").setValue(winnerPseudo)
                        lobbyRef.child("turn_history").get().addOnSuccessListener { snapshot ->
                            val updatedHistory = mutableListOf<String>()
                            snapshot.children.forEach { snap ->
                                snap.getValue(String::class.java)?.let { updatedHistory.add(it) }
                            }
                            updatedHistory.add(leaverMessage)
                            lobbyRef.child("turn_history").setValue(updatedHistory)
                        }

                        navController.navigate(Screens.Menu.name){
                            popUpTo(Screens.Menu.name){
                                inclusive = true
                            }
                        }
                    }) {
                        Text(text = stringResource(R.string.leave))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialogLeave = false
                    }) {
                        Text(text = stringResource(R.string.stay))
                    }
                }
            )
        }
    }
}