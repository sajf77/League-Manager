package com.sajad.leaguemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajad.leaguemanager.ui.theme.LeagueManagerTheme
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LeagueViewModel = viewModel()
            val theme by viewModel.theme.collectAsState()
            val fontSize by viewModel.fontSize.collectAsState()

            LeagueManagerTheme(darkTheme = theme == "dark", fontSize = fontSize) {
                LeagueManagerApp(viewModel = viewModel)
            }
        }
    }
}
