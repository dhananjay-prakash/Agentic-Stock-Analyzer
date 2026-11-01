package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.presentation.MainScreen
import com.example.presentation.MainViewModel
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.OmniVestTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as OmniVestApplication
        MainViewModel.provideFactory(
            portfolioRepository = app.portfolioRepository,
            securityRepository = app.securityRepository,
            researchRepository = app.researchRepository,
            alertRepository = app.alertRepository,
            watchlistRepository = app.watchlistRepository,
            database = app.database,
            dataTower = app.dataTower
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniVestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkNavyBackground
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
