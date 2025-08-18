package com.metraakladap.hexvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.metraakladap.hexvault.navigation.NavigationGraph
import com.metraakladap.hexvault.ui.theme.HexVaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HexVaultTheme {
                val navController = rememberNavController()
                NavigationGraph(navController)
            }
        }
    }
}

