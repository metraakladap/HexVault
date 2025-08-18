package com.metraakladap.hexvault.repository

import com.metraakladap.hexvault.network.CoinGeckoApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository @Inject constructor(
    private val coinGeckoApi: CoinGeckoApi
) {
    suspend fun getBtcUsdPrice(): Double? {
        val response = coinGeckoApi.getSimplePrice(
            ids = "bitcoin",
            vsCurrencies = "usd",
            include24hChange = true,
            includeLastUpdatedAt = true
        )
        return response["bitcoin"]?.get("usd")
    }
}


