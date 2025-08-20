package com.metraakladap.hexvault.crypto

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.DeterministicHierarchy
import org.bitcoinj.script.Script
import org.bitcoinj.core.SegwitAddress
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.TransactionOutPoint
import org.bitcoinj.core.TransactionInput
import org.bitcoinj.core.TransactionOutput
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.TransactionWitness
import org.bitcoinj.crypto.TransactionSignature
import org.bitcoinj.script.ScriptBuilder

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

    data class Utxo(
        val txid: String,
        val vout: Int,
        val valueSats: Long,
        val scriptPubKeyHex: String? = null
    )

    /**
     * Build and sign a simple P2WPKH transaction spending provided UTXOs to a recipient address.
     * Single sender key: m/84'/1'/0'/0/0
     */
    fun buildAndSignP2wpkh(
        utxos: List<Utxo>,
        toAddressBech32: String,
        amountSats: Long,
        feeSats: Long
    ): Transaction {
        val params: NetworkParameters = TestNet3Params.get()
        val mnemonic = seedManager.getMnemonicOnce() ?: error("Seed not initialized")
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

        val tx = Transaction(params)
        val totalIn = utxos.sumOf { it.valueSats }
        require(totalIn >= amountSats + feeSats) { "Insufficient funds" }

        // Outputs: recipient + change (back to sender)
        val to = SegwitAddress.fromBech32(params, toAddressBech32)
        tx.addOutput(Coin.valueOf(amountSats), to)
        val change = totalIn - amountSats - feeSats
        if (change > 0) {
            val changeAddr = SegwitAddress.fromKey(params, key)
            tx.addOutput(Coin.valueOf(change), changeAddr)
        }

        // Inputs
        utxos.forEach { u ->
            val outPoint = TransactionOutPoint(params, u.vout.toLong(), Sha256Hash.wrap(u.txid))
            val input = TransactionInput(params, tx, ByteArray(0), outPoint, Coin.valueOf(u.valueSats))
            tx.addInput(input)
        }

        // Sign all inputs with the derived key (P2WPKH witness)
        val ecKey: ECKey = key
        val scriptCode = ScriptBuilder.createOutputScript(org.bitcoinj.core.Address.fromKey(params, ecKey, Script.ScriptType.P2PKH))
        tx.inputs.forEachIndexed { index, input ->
            val value = Coin.valueOf(utxos[index].valueSats)
            val hash = tx.hashForWitnessSignature(index, scriptCode, value, Transaction.SigHash.ALL, false)
            val txSig = TransactionSignature(ecKey.sign(hash), Transaction.SigHash.ALL, false)
            val witness = TransactionWitness(2)
            witness.setPush(0, txSig.encodeToBitcoin())
            witness.setPush(1, ecKey.pubKey)
            input.witness = witness
        }

        return tx
    }
}


