package com.metraakladap.hexvault.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.metraakladap.hexvault.viewmodel.SettingsViewModel
import com.metraakladap.hexvault.R
import com.metraakladap.hexvault.ui.components.GradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(text = stringResource(id = R.string.go_back))
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
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.language), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LanguageRow(label = R.string.lang_en, selected = state.currentLocaleTag == "en") {
                    viewModel.setLocale("en")
                }
                LanguageRow(label = R.string.lang_uk, selected = state.currentLocaleTag == "uk") {
                    viewModel.setLocale("uk")
                }
                LanguageRow(label = R.string.lang_pl, selected = state.currentLocaleTag == "pl") {
                    viewModel.setLocale("pl")
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(label: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Text(text = stringResource(id = label))
        if (selected) {
            Text(text = "  •")
        }
    }
}


