package com.metraakladap.hexvault.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.metraakladap.hexvault.R
import com.metraakladap.hexvault.navigation.Screens
import com.metraakladap.hexvault.ui.components.ElevatedRoundedCard
import com.metraakladap.hexvault.ui.components.GradientBackground
import com.metraakladap.hexvault.ui.components.QrCode
import com.metraakladap.hexvault.viewmodel.MainViewModel
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
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.loadPrice()
        viewModel.loadWallet()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
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
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    val priceText = state.btcUsdPrice?.let {
                        String.format("$%.2f", it)
                    } ?: "—"
                    Text(
                        text = stringResource(id = R.string.btc_usd, priceText)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    state.errorMessage?.let {
                        Text(text = it)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        viewModel.loadPrice()
                    }) {
                        Text(stringResource(id = R.string.refresh_price))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    state.btcTestnetAddress?.let { addr ->
                        com.metraakladap.hexvault.ui.components.ElevatedRoundedCard {
                            Text(text = stringResource(id = R.string.your_address))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = addr)
                            Spacer(modifier = Modifier.height(8.dp))
                            state.confirmedBalanceSats?.let { bal ->
                                Text(text = stringResource(id = R.string.balance_sats, bal))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            QrCode(content = addr, size = 512)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = {
                                clipboard.setText(AnnotatedString(addr))
                                coroutineScope.launch { snackbarHostState.showSnackbar(message = copiedMsg) }
                            }) { Text(stringResource(id = R.string.copy)) }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    ElevatedRoundedCard {
                        var to by androidx.compose.runtime.remember {
                            androidx.compose.runtime.mutableStateOf(
                                ""
                            )
                        }
                        var amount by androidx.compose.runtime.remember {
                            androidx.compose.runtime.mutableStateOf(
                                ""
                            )
                        }
                        var feeRate by androidx.compose.runtime.remember {
                            androidx.compose.runtime.mutableStateOf(
                                state.feeRateSatsPerVb.toString()
                            )
                        }
                        Text(text = "UTXO:")
                        Spacer(modifier = Modifier.height(8.dp))
                        state.availableUtxos.forEach { u ->
                            val key = "${u.txid}:${u.vout}"
                            val checked = state.selectedUtxoKeys.contains(key)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = checked, onCheckedChange = {
                                    viewModel.toggleUtxoSelection(u.txid, u.vout)
                                })
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${u.txid.take(8)}...:${u.vout}  ${u.value} sats")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = feeRate,
                            onValueChange = {
                                feeRate = it
                                it.toLongOrNull()?.let { fr -> viewModel.setFeeRate(fr) }
                            },
                            label = { Text("Fee rate (sats/vB)") }
                        )
                        state.estimatedFeeSats?.let { fee ->
                            Spacer(modifier = Modifier.height(4.dp))
                            val vbytes = state.estimatedVBytes ?: 0
                            Text(text = "Estimated fee: ${fee} sats (${vbytes} vB)")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = to,
                            onValueChange = { to = it },
                            label = { Text(stringResource(id = R.string.recipient)) })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text(stringResource(id = R.string.amount_sats)) })
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            val a = amount.toLongOrNull() ?: 0L
                            if (viewModel.validateSend(to, a)) {
                                val fee = state.estimatedFeeSats ?: 0L
                                viewModel.sendTestnet(to, a, fee)
                            } else {
                                state.warningMessage?.let { msg ->
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            }
                        }, enabled = state.isSending.not()) {
                            Text(stringResource(id = R.string.send))
                        }
                        state.lastBroadcastTxId?.let { txid ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = stringResource(id = R.string.tx_sent, txid))
                        }
                    }
                }
            }
        }
    }
}