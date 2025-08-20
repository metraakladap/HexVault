package com.metraakladap.hexvault.data

data class MainState (
    val isLoading: Boolean = false,
    val isExchangeRateLoading: Boolean = false,
    val btcUsdPrice: Double? = null,
    val errorMessage: String? = null,
    val btcTestnetAddress: String? = null,
    val confirmedBalanceSats: Long? = null,
    val isSending: Boolean = false,
    val lastBroadcastTxId: String? = null,
    val availableUtxos: List<com.metraakladap.hexvault.network.BlockstreamUtxo> = emptyList(),
    val selectedUtxoKeys: Set<String> = emptySet(),
    val feeRateSatsPerVb: Long = 5L,
    val estimatedFeeSats: Long? = null,
    val estimatedVBytes: Int? = null,
    val warningMessage: String? = null,
)