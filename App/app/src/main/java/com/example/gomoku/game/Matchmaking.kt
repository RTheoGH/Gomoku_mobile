package com.example.gomoku.game

import androidx.navigation.NavHostController
import com.example.gomoku.nav.Screens
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

fun eloTolerance(temps: Int): Int {
    return when {
        temps < 20 -> 30
        temps < 40 -> 60
        temps < 60 -> 90
        else -> 120
    }
}

fun inSameTimePeriod(t1: Int, t2: Int): Boolean {
    val tp1 = when {
        t1 < 20 -> 0
        t1 < 40 -> 1
        t1 < 60 -> 2
        else -> 3
    }
    val tp2 = when {
        t2 < 20 -> 0
        t2 < 40 -> 1
        t2 < 60 -> 2
        else -> 3
    }
    return tp1 == tp2
}

suspend fun searchMatch(
    currentUid: String, username: String, elo: Int, profilePic: String,
    ranked: Boolean, blitz: Boolean,
    tempsEcoule: Int,
    rdb: FirebaseDatabase, navController: NavHostController
): Boolean {
    val matchmakingRef = rdb.getReference("matchmaking")
    val snapshot = matchmakingRef.get().await()
    val tolerance = eloTolerance(tempsEcoule)

    for (child in snapshot.children) {
        val otherUid = child.key ?: continue
        val otherRanked = child.child("ranked").getValue(Boolean::class.java) ?: false
        val otherBlitz = child.child("blitz").getValue(Boolean::class.java) ?: false
        val otherPseudo = child.child("pseudo").getValue(String::class.java) ?: "Player"
        val otherElo = child.child("elo").getValue(Int::class.java) ?: 1000
        val otherProfilePic = child.child("profile_pic").getValue(String::class.java) ?: ""
        val otherTimer = child.child("timer").getValue(Int::class.java) ?: 0

        val sameMode = (otherRanked == ranked) && (otherBlitz == blitz)
        val eloRange = abs(elo - otherElo) <= tolerance
        val validTimer = inSameTimePeriod(tempsEcoule, otherTimer)

        if ((otherUid != currentUid) && sameMode && eloRange && validTimer) {
            val lobbyId = "$currentUid$otherUid".take(20)

            createLobby(
                currentUid, username, profilePic,
                otherUid, otherPseudo, otherProfilePic,
                ranked, blitz,
                rdb, navController
            )

            matchmakingRef.child(otherUid).child("matched_lobby_id").setValue(lobbyId)
            matchmakingRef.child(currentUid).removeValue()
            return true
        }
    }

    matchmakingRef.child(currentUid).setValue(
        mapOf(
            "pseudo" to username,
            "elo" to elo,
            "profile_pic" to profilePic,
            "ranked" to ranked,
            "blitz" to blitz,
            "created_at" to System.currentTimeMillis(),
            "timer" to tempsEcoule
        )
    )
    return false
}

fun createLobby(
    uid1: String, pseudo1: String, pic1: String,
    uid2: String, pseudo2: String, pic2: String,
    ranked: Boolean, blitz: Boolean,
    rdb: FirebaseDatabase, navController: NavHostController
) {
    val lobbyId = "$uid1$uid2".take(20)
    val board = List(15) { i ->
        List(15) { j ->
            gomokuCellToMap(GomokuCell(i, j, CellState.EMPTY))
        }
    }

    val player1Data = mapOf(
        "uid" to uid1,
        "pseudo" to pseudo1,
        "profile_pic" to pic1,
        "timer" to 300
    )
    val player2Data = mapOf(
        "uid" to uid2,
        "pseudo" to pseudo2,
        "profile_pic" to pic2,
        "timer" to 300
    )

    val lobbyData = mapOf(
        "host" to pseudo1,
        "player1" to player1Data,
        "player2" to player2Data,
        "winner" to "",
        "status" to "started",
        "ranked" to ranked,
        "blitz" to blitz,
        "created_at" to System.currentTimeMillis(),
        "board" to board,
        "turn" to 0,
        "turn_history" to emptyList<String>(),
        "chat" to emptyList<String>()
    )

    rdb.getReference("lobbies").child(lobbyId).setValue(lobbyData)
    navController.navigate("${Screens.Online_game.name}/$lobbyId")
}

