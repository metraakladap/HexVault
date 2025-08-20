package com.metraakladap.hexvault.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class BlockstreamUtxo(
    val txid: String,
    val vout: Int,
    val value: Long,
    val status: Status
) {
    data class Status(val confirmed: Boolean)
}

interface BlockstreamApi {
    @GET("api/address/{address}/utxo")
    suspend fun getAddressUtxos(@Path("address") address: String): List<BlockstreamUtxo>

    @GET("api/blocks/tip/height")
    suspend fun getTipHeight(): ResponseBody

    @POST("api/tx")
    suspend fun broadcast(@retrofit2.http.Body rawTx: RequestBody): ResponseBody

    @GET("api/tx/{txid}/hex")
    suspend fun getTxHex(@Path("txid") txid: String): ResponseBody
}


