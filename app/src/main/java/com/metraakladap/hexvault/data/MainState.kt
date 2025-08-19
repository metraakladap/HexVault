package com.metraakladap.hexvault.data

data class MainState (
    val isLoading: Boolean = false,
    val btcUsdPrice: Double? = null,
    val errorMessage: String? = null,
    val btcTestnetAddress: String? = null,
)