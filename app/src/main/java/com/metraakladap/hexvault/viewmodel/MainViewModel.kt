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
                val utxos = walletRepository.fetchUtxos(address).filter { it.status.confirmed }
                val balance = utxos.sumOf { it.value }
                _state.value = _state.value.copy(
                    isLoading = false,
                    btcTestnetAddress = address,
                    confirmedBalanceSats = balance,
                    availableUtxos = utxos,
                )
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }

    fun toggleUtxoSelection(txid: String, vout: Int) {
        val key = "$txid:$vout"
        val current = _state.value.selectedUtxoKeys.toMutableSet()
        if (current.contains(key)) current.remove(key) else current.add(key)
        _state.value = _state.value.copy(selectedUtxoKeys = current)
        estimateFee()
    }

    fun setFeeRate(satsPerVb: Long) {
        _state.value = _state.value.copy(feeRateSatsPerVb = satsPerVb)
        estimateFee()
    }

    private fun estimateFee() {
        val selected = _state.value.availableUtxos.filter { _state.value.selectedUtxoKeys.contains("${'$'}{it.txid}:${'$'}{it.vout}") }
        if (selected.isEmpty()) {
            _state.value = _state.value.copy(estimatedVBytes = null, estimatedFeeSats = null)
            return
        }
        // Rough estimate for P2WPKH: ~68 vB per input, ~31 vB per output + 10 overhead
        val numInputs = selected.size
        val numOutputs = 2 // send + change (worst case)
        val vbytes = 10 + numInputs * 68 + numOutputs * 31
        val fee = vbytes * _state.value.feeRateSatsPerVb
        _state.value = _state.value.copy(estimatedVBytes = vbytes, estimatedFeeSats = fee)
    }

    fun validateSend(toBech32: String, amountSats: Long): Boolean {
        if (!toBech32.startsWith("tb1")) {
            _state.value = _state.value.copy(warningMessage = "Invalid testnet bech32 address")
            return false
        }
        val balance = _state.value.confirmedBalanceSats ?: 0
        val fee = _state.value.estimatedFeeSats ?: 0
        if (amountSats <= 0) {
            _state.value = _state.value.copy(warningMessage = "Amount must be > 0")
            return false
        }
        if (amountSats + fee > balance) {
            _state.value = _state.value.copy(warningMessage = "Insufficient funds including fee")
            return false
        }
        _state.value = _state.value.copy(warningMessage = null)
        return true
    }

    fun sendTestnet(toBech32: String, amountSats: Long, feeSats: Long) {
        _state.value = _state.value.copy(isSending = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val selectedKeys = _state.value.selectedUtxoKeys
                val selectedUtxos = _state.value.availableUtxos.filter { selectedKeys.contains("${'$'}{it.txid}:${'$'}{it.vout}") }
                val utxos = selectedUtxos.map { com.metraakladap.hexvault.crypto.WalletManager.Utxo(it.txid, it.vout, it.value) }
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