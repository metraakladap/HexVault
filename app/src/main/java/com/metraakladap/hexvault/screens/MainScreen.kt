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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.metraakladap.hexvault.viewmodel.MainViewModel
import com.metraakladap.hexvault.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import com.metraakladap.hexvault.navigation.Screens
import com.metraakladap.hexvault.ui.components.GradientBackground
import com.metraakladap.hexvault.ui.components.QrCode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val copiedMsg = stringResource(id = R.string.copied)

    LaunchedEffect(Unit) { viewModel.loadPrice() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        GradientBackground {
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

                    Spacer(modifier = Modifier.height(24.dp))
                    state.btcTestnetAddress?.let { addr ->
                        Text(text = stringResource(id = R.string.your_address))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = addr)
                        Spacer(modifier = Modifier.height(12.dp))
                        QrCode(content = addr, size = 512)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            clipboard.setText(AnnotatedString(addr))
                            coroutineScope.launch { snackbarHostState.showSnackbar(message = copiedMsg) }
                        }) { Text(stringResource(id = R.string.copy)) }
                    }
                }
            }
        }
    }
}