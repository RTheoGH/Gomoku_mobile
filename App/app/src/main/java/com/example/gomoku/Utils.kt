package com.example.gomoku

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomoku.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Back(navController: NavHostController){
    IconButton(
        onClick = { navController.navigate(Screens.Menu.name) },
        modifier = Modifier
            .padding(4.dp)
            .size(32.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
        )
    }
}

@Composable
fun Chargement(){
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun ModeText(s : String){
    Text(
        text = "Mode : $s",
        fontSize = 30.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun SecondaryText(text : String){
    Text(
        text = text,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun SwitchWithIcon(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Switch(
        modifier = Modifier.padding(4.dp),
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

@SuppressLint("DefaultLocale")
fun formatSecondsToTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@SuppressLint("DiscouragedApi")
@Composable
fun Custom_row(i : Int, timer : String, player : String, pp : String){
    val context = LocalContext.current
    Row(
        modifier = when (i) {
            1 -> Modifier
                .fillMaxWidth()
                .padding(end = 48.dp)
            2 -> Modifier
                .fillMaxWidth()
                .padding(start = 48.dp)
            else -> Modifier.fillMaxWidth()
        },
        horizontalArrangement = when(i) {
            1 -> Arrangement.End
            2 -> Arrangement.Start
            else -> Arrangement.Center
        },
        verticalAlignment = Alignment.CenterVertically
    ){
        when (i) {
            1 -> {
                if(timer != "") Custom_card(timer)
                if(pp != ""){
                    val resId = remember(pp) {
                        context.resources.getIdentifier(pp, "drawable", context.packageName)
                    }
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Custom_card(player)
                Canvas(modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)){
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
            }

            2 -> {
                Canvas(modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)){
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
                Custom_card(player)
                if(pp != ""){
                    val resId = remember(pp) {
                        context.resources.getIdentifier(pp, "drawable", context.packageName)
                    }
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(30.dp)
                    )
                }
                if(timer != "") Custom_card(timer)
            }
        }
    }
}

@Composable
fun Custom_card(text : String){
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun recup_moi(auth: FirebaseAuth, db: FirebaseFirestore, onRes : (String) -> Unit) {
    var current_user = auth.currentUser ?: return onRes("")

    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            val pseudo = res.getString("pseudo") ?: ""
            onRes(pseudo)
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error")
            onRes("")
        }
}

fun loadFriendsAndRequests(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onRes: (friends: List<String>, requests: List<String>) -> Unit
){
    val current_user = auth.currentUser!!
    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            val requests =
                (res.get("requests") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            val friends =
                (res.get("friends") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            onRes(friends, requests)
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error getting current user")
            onRes(emptyList(), emptyList())
        }
}

@Composable
fun Recup_request(
    request : String,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onRefresh: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = request,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row{
                IconButton(
                    onClick = {
                        accept_friend_request(auth, db, request){
                            remove_friend_request(auth, db, request){
                                onRefresh()
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Accepter",
                        tint = Color(0xFF4CAF50)
                    )
                }
                IconButton(
                    onClick = {
                        remove_friend_request(auth, db, request){
                            onRefresh()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Refuser",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun Recup_friend(
    friend: String,
    pps: MutableState<Map<String, String>>,
    elos: MutableState<Map<String, Int>>,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onRefresh: () -> Unit
){
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Log.i("TAG", "Verif_images: $friend")
            Log.i("TAG", "Verif_images: $pps")
            Log.i("TAG", "Verif_images: ${pps.value[friend]}")
            if(pps.value[friend] != null && pps.value[friend] != ""){
                val resId = remember(pps.value[friend]) {
                    context.resources.getIdentifier(pps.value[friend], "drawable", context.packageName)
                }
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                text = friend,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Custom_card(elos.value[friend].toString())
                IconButton(
                    onClick = {
                        remove_friend(auth, db, friend, onRefresh)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Refuser",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

fun accept_friend_request(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    request : String,
    onComplete: () -> Unit
){
    val current_user = auth.currentUser!!

    // Add friend to current user
    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            val current_p = res.get("pseudo").toString()
            db.collection("users")
                .document(current_user.uid)
                .update("friends", FieldValue.arrayUnion(request))
                .addOnSuccessListener {
                    db.collection("users").whereEqualTo("pseudo", request).get()
                        .addOnSuccessListener {
                            val target = it.documents.first().id
                            db.collection("users")
                                .document(target)
                                .update("friends", FieldValue.arrayUnion(current_p))
                                .addOnSuccessListener {
                                    onComplete()
                                }
                                .addOnFailureListener {
                                    Log.i("TAG", "Profile: Error getting target1 user")
                                }
                        }
                        .addOnFailureListener {
                            Log.i("TAG", "Profile: Error getting target2 user")
                        }
                }
                .addOnFailureListener {
                    Log.i("TAG", "Profile: Error updating current user")
                }
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error getting current user")
        }
}

fun remove_friend_request(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    request : String,
    refresh : () -> Unit
){
    val current_user = auth.currentUser!!
    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener { res ->
            db.collection("users")
                .document(current_user.uid)
                .update("requests", FieldValue.arrayRemove(request))
                .addOnSuccessListener {
                    Log.i("TAG", "Profile: Request removed")
                    refresh()
                }
                .addOnFailureListener {
                    Log.i("TAG", "Profile: Error removing request")
                }
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error getting current user")
        }
}

fun remove_friend(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    friend : String,
    refresh : () -> Unit
){
    val current_user = auth.currentUser!!
    var current_p = ""

    db.collection("users").document(current_user.uid).get()
        .addOnSuccessListener {
            current_p = it.get("pseudo").toString()
            db.collection("users")
                .document(current_user.uid)
                .update("friends", FieldValue.arrayRemove(friend))
                .addOnSuccessListener {
                    //remove friend to the other user
                    db.collection("users").whereEqualTo("pseudo", friend).get()
                        .addOnCompleteListener {
                            val target = it.result.documents.first().id
                            db.collection("users")
                                .document(target)
                                .update("friends", FieldValue.arrayRemove(current_p))
                                .addOnSuccessListener {
                                    refresh()
                                }
                                .addOnFailureListener {
                                    Log.i("TAG", "Profile: Error getting target current user")
                                }
                        }
                        .addOnFailureListener {
                            Log.i("TAG", "Profile: Error getting target2 user")
                        }
                }
                .addOnFailureListener {
                    Log.i("TAG", "Profile: Error updating current user")
                }
        }
        .addOnFailureListener {
            Log.i("TAG", "Profile: Error getting current user")
        }
}

fun joinLobbyAndRemoveInvitation(inviter: String, lobbyId: String){
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val rdb = FirebaseDatabase.getInstance("https://gomoku-76114-default-rtdb.europe-west1.firebasedatabase.app")
    val user = auth.currentUser!!.uid

    db.collection("users").document(user).get().addOnSuccessListener { res ->
        val pseudo = res.get("pseudo") as? String ?: return@addOnSuccessListener
        val profilePic = res.get("profile_pic") as? String ?: return@addOnSuccessListener

        val player2Data = mapOf(
            "uid" to user,
            "pseudo" to pseudo,
            "profile_pic" to profilePic,
            "timer" to 300
        )

        val lobbyRef = rdb.getReference("lobbies").child(lobbyId)
        var currentJ2 = ""
        lobbyRef.child("player2")
            .child("pseudo").get().addOnSuccessListener {
                currentJ2 = it.value.toString()
            }

        Log.i("TAG", "joinLobbyAndRemoveInvitation: $currentJ2")

        if(currentJ2.isEmpty()){
            lobbyRef.child("player2").setValue(player2Data)
            db.collection("users").document(user)
                .update("invitation", FieldValue.arrayRemove(mapOf("inviter" to inviter, "lobbyId" to lobbyId)))
        }else{
            return@addOnSuccessListener
        }
    }
}

fun removeInvitationFromFriends(inviter: String, lobbyId: String){
    val db = FirebaseFirestore.getInstance()

    db.collection("users").whereEqualTo("pseudo", inviter).get().addOnSuccessListener { res ->
        if(!res.isEmpty){
            val doc = res.documents[0]
            val friends = doc.get("friends") as? List<String> ?: emptyList()
            Log.i("TAG", "removeInvitationFromFriends: friends : $friends")

            db.collection("users").whereIn("pseudo", friends).get().addOnSuccessListener { fRes ->
                val friendsDocs = fRes.documents
                Log.i("TAG", "removeInvitationFromFriends: friendsDocs : $friendsDocs")

                db.runTransaction { transaction ->
                    friendsDocs.forEach { f ->
                        val fRef = db.collection("users").document(f.id)
                        val fS = transaction.get(fRef)
                        val invitations = fS.get("invitation") as? List<Map<String, String>> ?: emptyList()
                        val newInvitations = invitations.filter { it["lobbyId"] != lobbyId }

                        if(newInvitations.size != invitations.size){
                            transaction.update(fRef, "invitation", newInvitations)
                            Log.i("TAG", "removeInvitationFromFriends: invitations updated")
                        }else{
                            Log.i("TAG", "removeInvitationFromFriends: invitations not updated")
                        }
                    }
                }.addOnSuccessListener {
                    Log.i("TAG", "Invitations supprimées")
                }.addOnFailureListener {
                    Log.e("TAG", "Erreur transaction")
                }
            }.addOnFailureListener {
                Log.e("TAG", "Erreur recupération amis")
            }
        } else {
            Log.e("TAG", "Aucun utilisateur")
        }
    }.addOnFailureListener {
        Log.e("TAG", "Erreur recupération utilisateur")
    }
}

@Composable
fun ChooseDifficulty(choice: MutableState<String>, select: List<String>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        select.forEach { difficulty ->
            RadioButton(
                selected = choice.value == difficulty,
                onClick = { choice.value = difficulty }
            )
            Text(
                text = when(difficulty) {
                    "Easy" -> stringResource(R.string.easy)
                    "Medium" -> stringResource(R.string.medium)
                    "Hard" -> stringResource(R.string.hard)
                    else -> stringResource(R.string.easy)
                }
            )
        }
    }
}