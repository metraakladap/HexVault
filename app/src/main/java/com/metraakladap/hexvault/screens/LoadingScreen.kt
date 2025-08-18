package com.metraakladap.hexvault.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.metraakladap.hexvault.viewmodel.LoadingScreenViewModel

@Composable
fun LoadingScreen(
    viewModel: LoadingScreenViewModel,
) {
    val state by viewModel.state.collectAsState()

}