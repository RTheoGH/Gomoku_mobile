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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gomoku.Back
import com.example.gomoku.Custom_card
import com.example.gomoku.Custom_row
import com.example.gomoku.R
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
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

        Spacer(modifier = Modifier.height(164.dp))

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

                                val lobbyData = mapOf(
                                    "host" to uid_name,
                                    "password" to password.trim(),
                                    "player1" to uid_name,
                                    "player2" to "",
                                    "status" to "waiting",
                                    "created_at" to System.currentTimeMillis(),
                                    "board" to board
                                    //TODO : ajouter l'état du plateau de jeu (Liste de GomokuCell) (vide par défaut)
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
                                val currentPlayer1 = snapshot.child("player1").getValue(String::class.java)
                                val currentPlayer2 = snapshot.child("player2").getValue(String::class.java)

                                when{
                                    lobbyPassword != enteredPassword -> errorMessage = "Mot de passe incorrect"
                                    currentPlayer1 == uid_name || currentPlayer2 == uid_name -> errorMessage = "Vous êtes déjà dans cette partie"
                                    !currentPlayer2.isNullOrEmpty() -> errorMessage = "Partie pleine"
                                    else -> {
                                        lobbyRef.child("player2").setValue(uid_name)
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
            player1 = snapshot.child("player1").getValue(String::class.java) ?: ""
            player2 = snapshot.child("player2").getValue(String::class.java) ?: ""
            password = snapshot.child("password").getValue(String::class.java) ?: ""
            status = snapshot.child("status").getValue(String::class.java) ?: "waiting"

            if(status == "started"){
                navController.navigate("${Screens.Online_game.name}/$lobbyId")
            }else if (player2.isNotEmpty()){
                lobbyRef.child("status").setValue("ready")
            }

//            isHost = player1 == currentUidName
//            if(player2.isNotEmpty()){
//                lobbyRef.child("status").setValue("ready")
//            }
//            canStart = player2.isNotEmpty() && isHost && status == "ready"
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

        Spacer(modifier = Modifier.height(164.dp))

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
            Custom_card(player2)

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
                            lobbyRef.child("player2").setValue("")
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
    //TODO : afficher le jeu
    val context = LocalContext.current

    var showDialogWin by remember { mutableStateOf(false) }
    var showDialogLeave by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var player1 by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }
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
        listState.animateScrollToItem(turn_history.size - 1)
    }

    val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
    val valueEventListener = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            player1 = snapshot.child("player1").getValue(String::class.java) ?: ""
            player2 = snapshot.child("player2").getValue(String::class.java) ?: ""
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

    Column(modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)) {
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

                        if (check_win(board, x, y, 15)) {
                            isFinished = true
                            showDialogWin = true
                            println("gagné !!!!!!")
                            //TODO : enregistrer la partie dans l'historique
                        }

                        playerTurn = 1 - playerTurn
                        println("Tour du joueur : $playerTurn")

                        val updatedBoard = boardToFirebaseFormat(board)
                        lobbyRef.child("board").setValue(updatedBoard)
                    }
                }
            )

            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Custom_row(2,"",player1)

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            LazyColumn(state = listState, modifier = Modifier.background(Color.LightGray).fillMaxWidth().padding(vertical = 10.dp)) {
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

            if(showDialogWin){
                val winner = if(playerTurn == 1) player1 else player2
                AlertDialog(
                    onDismissRequest = { showDialogWin = false },
                    title = { Text(text = stringResource(R.string.game_over)) },
                    text = { Text(text = winner+" "+stringResource(R.string.win_offline)) },
                    confirmButton = {
                        Button(onClick = {
                            showDialogWin = false
                            val route = "${Screens.Offline_game.name}/$player1/$player2"
                            navController.navigate(route){
                                popUpTo(route){
                                    inclusive = true
                                }
                            }
                        }) {
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
                onDismissRequest = { showDialogLeave = false },
                title = { Text(text = "Quitter ?") },
                text = { Text(text = "Voulez-vous vraiment quitter la partie ?") },
                confirmButton = {
                    Button(onClick = {
                        showDialogLeave = false
                        //TODO : faire gagner l'autre joueur
                        val route = navController.navigate(Screens.Menu.name)
                        navController.navigate(route){
                            popUpTo(route){
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