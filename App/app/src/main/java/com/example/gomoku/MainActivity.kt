package com.example.gomoku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gomoku.ui.theme.GomokuTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        auth.signOut()

        db = Firebase.firestore
        Log.i("TAG", "Ya quoi la 1 : ${auth.currentUser}")
        db.collection("users")
            .document("Azouuuu")
            .get().addOnSuccessListener { res ->
                Log.i("TAG", "Ca a marché :) : ${res.data}")
            }.addOnFailureListener { e ->
                Log.i("TAG", "Pas marché :( : ", e)
            }

        auth.signInWithEmailAndPassword("reynier.theo@gmail.com", "abcdef")
        Log.i("TAG", "Ya quoi la 2 : ${auth.currentUser}")

        enableEdgeToEdge()
        setContent {
            GomokuTheme {
                App()
            }
        }
    }
}