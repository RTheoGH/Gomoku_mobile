package com.example.gomoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gomoku.ui.theme.GomokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GomokuTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    navController: NavHostController = rememberNavController()
){
    var titre by remember { mutableStateOf("Gomoku") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(titre) }
            )
        },
    ) { innerPadding ->
        //Menu(innerPadding)
        NavHost(
            navController = navController,
            startDestination = Screens.Menu.name
        ){
            composable(route = Screens.Menu.name) {
                Menu(innerPadding, navController)
            }
            composable(route = Screens.Sign_in.name) {
                Sign_in(innerPadding, navController)
            }
            composable(route = Screens.Sign_up.name) {
                Sign_up(innerPadding, navController)
            }
            composable(route = Screens.Sign_out.name) {
                Sign_out(innerPadding, navController)
            }
            composable(route = Screens.Profile.name) {
                Profile(innerPadding, navController)
            }
            composable(route = Screens.EditProfile.name) {
                EditProfile(innerPadding, navController)
            }
        }
    }
}