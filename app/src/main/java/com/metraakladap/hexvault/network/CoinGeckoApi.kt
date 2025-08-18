package com.metraakladap.hexvault.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {

    // https://docs.coingecko.com/reference/simple-price
    @GET("/api/v3/simple/price")
    suspend fun getSimplePrice(
        @Query("ids") ids: String, // e.g. "bitcoin"
        @Query("vs_currencies") vsCurrencies: String, // e.g. "usd"
        @Query("include_24hr_change") include24hChange: Boolean = true,
        @Query("include_last_updated_at") includeLastUpdatedAt: Boolean = true
    ): Map<String, Map<String, Double>>
}


