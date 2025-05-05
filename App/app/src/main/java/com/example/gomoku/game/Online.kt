package com.example.gomoku.game

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Custom_card
import com.example.gomoku.Custom_row
import com.example.gomoku.R
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Online(pad : PaddingValues, navController: NavHostController){
    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)){
        Back(navController)

        Spacer(modifier = Modifier.height(164.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(Screens.Online_create.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "créé")
            }
            Button(
                onClick = {
                    navController.navigate(Screens.Online_join.name)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "rejoint")
            }
        }
    }
    //TODO : afficher les boutons pour créer une partie ou rejoindre une partie
}

@Composable
fun Online_create(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase
){
    var lobby_name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Spacer(modifier = Modifier.height(128.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lobby_name,
                onValueChange = { if(it.length <= 20) lobby_name = it },
                label = { Text(text = "Nom de la partie") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if(it.length <= 10) password = it },
                label = { Text(text = "Mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val lobbyId = lobby_name.trim()
                    val uid = auth.currentUser!!.uid
                    var uid_name = ""

                    if(lobbyId.isNotEmpty()){
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener {
                                uid_name = it.get("pseudo").toString()
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
                                    "pseudo" to uid_name
                                )

                                val lobbyData = mapOf(
                                    "host" to uid_name,
                                    "password" to password.trim(),
                                    /*TODO : son uid et son nom*/
                                    "player1" to player1Data,
                                    "player2" to "",
                                    "winner" to "",
                                    "status" to "waiting",
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
                                    errorMessage = "Lobby deja existant"
                                }
                            }.addOnFailureListener {
                                errorMessage = "Erreur lors de la récupération des données de l'utilisateur"
                            }
                    }
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = "Creer")
            }
        }
    }
    //TODO : créer une partie en demandant le nom de la partie et mdp
}

@Composable
fun Online_join(
    pad : PaddingValues,
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    rdb: FirebaseDatabase
){
    //TODO : rejoindre une partie en demandant le nom de la partie et mdp
    var lobby_name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
        Back(navController)

        Spacer(modifier = Modifier.height(128.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lobby_name,
                onValueChange = { if(it.length <= 20) lobby_name = it },
                label = { Text(text = "Nom de la partie") },
                modifier = Modifier.padding(4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if(it.length <= 10) password = it },
                label = { Text(text = "Mot de passe") },
                modifier = Modifier.padding(4.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val lobbyId = lobby_name.trim()
                    val enteredPassword = password.trim()
                    val uid = auth.currentUser!!.uid
                    var uid_name = ""

                    if(lobbyId.isEmpty() || uid == ""){
                        errorMessage = "Veuillez remplir tous les champs"
                        return@Button
                    }

                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            uid_name = userDoc.get("pseudo").toString()

                            val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
                            lobbyRef.get().addOnSuccessListener { snapshot ->
                                if (!snapshot.exists()) {
                                    errorMessage = "Partie introuvable"
                                    return@addOnSuccessListener
                                }

                                val lobbyPassword = snapshot.child("password").getValue(String::class.java)
                                val currentPlayer1 = snapshot.child("player1").child("pseudo").getValue(String::class.java)
                                val currentPlayer2 = snapshot.child("player2").child("pseudo").getValue(String::class.java)

                                when{
                                    lobbyPassword != enteredPassword -> errorMessage = "Mot de passe incorrect"
                                    currentPlayer1 == uid_name || currentPlayer2 == uid_name -> errorMessage = "Vous êtes déjà dans cette partie"
                                    !currentPlayer2.isNullOrEmpty() -> errorMessage = "Partie pleine"
                                    else -> {
                                        val player2Data = mapOf(
                                            "uid" to uid,
                                            "pseudo" to uid_name
                                        )

                                        lobbyRef.child("player2").setValue(player2Data)
                                            .addOnSuccessListener {
                                                navController.navigate(Screens.Online_lobby.name + "/$lobbyId")
                                            }
                                            .addOnFailureListener {
                                                errorMessage = "Erreur lors de la connexion à la partie"
                                            }
                                    }
                                }
                            }.addOnFailureListener {
                                errorMessage = "Erreur de connexion à la base de données"
                            }
                        }
                }
            ){
                Text(text = "Rejoindre")
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
    //TODO : afficher la salle d'attente avec les deux joueurs
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var player1 by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("waiting") }
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

            if(status == "started"){
                navController.navigate("${Screens.Online_game.name}/$lobbyId")
            }else if (player2.isNotEmpty()){
                lobbyRef.child("status").setValue("ready")
            }
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

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {

        Spacer(modifier = Modifier.height(128.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Mode : Online")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Salle :")
            Custom_card(lobbyId)

            Text("MDP | Status")
            Custom_card("$password | $status")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Joueur 1 :")
            Custom_card(player1)

            Text("Joueur 2 :")
            if(player2.isNotEmpty()){
                Custom_card(player2)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        if(isHost){
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
                    Text(text = "Quitter")
                }
                Button(
                    onClick = {
                        lobbyRef.child("status").setValue("started")
                    },
                    modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f),
                    enabled = canStart
                ) {
                    Text(text = "Lancer")
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

    var player1 by remember { mutableStateOf("") }
    var player1uid by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }
    var player2uid by remember { mutableStateOf("") }

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

    //TODO : envoyer le playerTurn sur realtime database ainsi que le turn_history

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
            player2 = snapshot.child("player2").child("pseudo").getValue(String::class.java) ?: ""
            player2uid = snapshot.child("player2").child("uid").getValue(String::class.java) ?: ""
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
            if(winner != "" && isFinished){
                showDialogWin = true
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
            Custom_row(1,"",player2)
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
                            isFinished = true
                            showDialogWin = true
                            winner = if(playerTurn == 0) player1 else player2
                            lobbyRef.child("status").setValue("finished")
                            lobbyRef.child("winner").setValue(winner)

                            println("gagné !!!!!!")
                            //TODO : enregistrer la partie dans l'historique
                        }

                        playerTurn = 1 - playerTurn
                        lobbyRef.child("turn").setValue(playerTurn)
                        lobbyRef.child("board").setValue(boardToFirebaseFormat(board))
                    }
                }
            )

            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Custom_row(2,"",player1)

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
                        val player = if(playerTurn == 1) player1 else player2
                        Text(modifier = Modifier.padding(horizontal = 5.dp), text = player+" "+ stringResource(R.string.win_offline))
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
                    text = { Text(text = winner+" "+stringResource(R.string.win_offline)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialogWin = false
                                val route = "${Screens.Offline_game.name}/$player1/$player2"
                                navController.navigate(route){
                                    popUpTo(route){
                                        inclusive = true
                                    }
                                }
                            },
                            enabled = false
                        ) {
                            Text(text = stringResource(R.string.replay))
                        }
                    },
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
                title = { Text(text = "Quitter ?") },
                text = { Text(text = "Voulez-vous vraiment quitter la partie ?") },
                confirmButton = {
                    Button(onClick = {
                        showDialogLeave = false
                        //TODO : faire gagner l'autre joueur
                        val currentUid = auth.currentUser!!.uid

                        val winnerPseudo = if(currentUid == player1uid) player2 else player1
                        val leaverPseudo = if(currentUid == player1uid) player1 else player2
                        val leaverMessage = "$leaverPseudo a quitté la partie"

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
                        Text(text = "Quitter")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialogLeave = false
                    }) {
                        Text(text = "Rester")
                    }
                }
            )
        }
    }
}