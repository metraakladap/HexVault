package com.metraakladap.hexvault.repository

import com.metraakladap.hexvault.crypto.WalletManager
import com.metraakladap.hexvault.network.BlockstreamApi
import com.metraakladap.hexvault.network.BlockstreamUtxo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletManager: WalletManager,
    private val blockstreamApi: BlockstreamApi
) {
    suspend fun getPrimaryAddress(): String = walletManager.getPrimaryTestnetAddress()

    suspend fun fetchConfirmedBalance(address: String): Long {
        val utxos = blockstreamApi.getAddressUtxos(address)
        return utxos.filter { it.status.confirmed }.sumOf { it.value }
    }

    suspend fun fetchUtxos(address: String): List<BlockstreamUtxo> =
        blockstreamApi.getAddressUtxos(address)

    suspend fun broadcastRawTx(hex: String): String {
        val body: RequestBody = hex.toRequestBody("text/plain".toMediaType())
        val resp = blockstreamApi.broadcast(body)
        return resp.string()
    }
}


