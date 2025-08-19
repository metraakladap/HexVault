package com.metraakladap.hexvault.crypto

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.DeterministicHierarchy
import org.bitcoinj.script.Script
import org.bitcoinj.core.SegwitAddress

class WalletManager(private val seedManager: SeedManager) {

    /**
     * Returns primary BTC address for testnet using BIP84 path m/84'/1'/0'/0/0 (bech32 p2wpkh)
     */
    fun getPrimaryTestnetAddress(): String {
        val mnemonic = seedManager.getMnemonicOnce()
            ?: throw IllegalStateException("Seed not initialized")

        val seed = MnemonicCode.toSeed(mnemonic, "")
        val master = HDKeyDerivation.createMasterPrivateKey(seed)
        val hierarchy = DeterministicHierarchy(master)
        val path = listOf(
            ChildNumber(84, true),
            ChildNumber(1, true),
            ChildNumber(0, true),
            ChildNumber.ZERO,
            ChildNumber.ZERO
        )
        val key = hierarchy.get(path, true, true)
        val params: NetworkParameters = TestNet3Params.get()
        return SegwitAddress.fromKey(params, key).toBech32()
    }
}


