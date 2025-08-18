package com.metraakladap.hexvault.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.metraakladap.hexvault.viewmodel.MainViewModel
import com.metraakladap.hexvault.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import com.metraakladap.hexvault.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadPrice() }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screens.Settings.route) }) {
                        Text(text = stringResource(id = R.string.settings))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                val priceText = state.btcUsdPrice?.let { String.format("$%.2f", it) } ?: "—"
                Text(text = stringResource(id = R.string.btc_usd, priceText))
                Spacer(modifier = Modifier.height(12.dp))
                state.errorMessage?.let { Text(text = it) }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.loadPrice() }) { Text(stringResource(id = R.string.refresh_price)) }
            }
        }
    }
}