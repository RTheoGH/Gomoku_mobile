package com.example.gomoku

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Menu(pad : PaddingValues){
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(pad).padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = { },
                modifier = Modifier.padding(4.dp).padding(end = 64.dp),
            ) {
                Text(text = stringResource(id = R.string.friends))
            }
            Button(
                onClick = { },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = "Elo : 2000")
            }

            Box{
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.padding(4.dp).size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier.size(72.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.sign_in)) },
                        onClick = { expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.sign_up)) },
                        onClick = { expanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(128.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Button(
                onClick = { },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(id = R.string.play_offline))
            }
            Button(
                onClick = { },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(id = R.string.play_online))
            }
            Button(
                onClick = { },
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.6f)
            ) {
                Text(text = stringResource(id = R.string.play_asynchronus))
            }
        }

    }
}