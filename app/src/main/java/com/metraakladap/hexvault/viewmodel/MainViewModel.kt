package com.metraakladap.hexvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metraakladap.hexvault.data.MainState
import com.metraakladap.hexvault.repository.PriceRepository
import com.metraakladap.hexvault.crypto.WalletManager
import com.metraakladap.hexvault.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val walletManager: WalletManager,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun loadPrice() {
        viewModelScope.launch {
            try {
                val price = priceRepository.getBtcUsdPrice()
                _state.value = _state.value.copy(
                    isLoading = false,
                    btcUsdPrice = price,
                )
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }

    fun loadWallet() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val address = walletManager.getPrimaryTestnetAddress()
                val balance = walletRepository.fetchConfirmedBalance(address)
                _state.value = _state.value.copy(
                    isLoading = false,
                    btcTestnetAddress = address,
                    confirmedBalanceSats = balance,
                )
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }

    fun sendTestnet(toBech32: String, amountSats: Long, feeSats: Long) {
        _state.value = _state.value.copy(isSending = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val fromAddr = walletManager.getPrimaryTestnetAddress()
                val utxos = walletRepository.fetchUtxos(fromAddr)
                    .filter { it.status.confirmed }
                    .map { com.metraakladap.hexvault.crypto.WalletManager.Utxo(it.txid, it.vout, it.value) }
                val tx = walletManager.buildAndSignP2wpkh(utxos, toBech32, amountSats, feeSats)
                val hex = tx.unsafeBitcoinSerialize().joinToString(separator = "") { String.format("%02x", it) }
                val txid = walletRepository.broadcastRawTx(hex)
                _state.value = _state.value.copy(isSending = false, lastBroadcastTxId = txid)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isSending = false, errorMessage = t.message)
            }
        }
    }
}