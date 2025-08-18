package com.metraakladap.hexvault.crypto

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.bitcoinj.crypto.MnemonicCode
import java.security.SecureRandom

class SeedManager(private val appContext: Context) {

    private val masterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            appContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun hasSeed(): Boolean = prefs.contains(KEY_SEED_MNEMONIC)

    fun generateAndStoreMnemonic(): List<String> {
        check(!hasSeed()) { "Seed already exists" }
        val entropy = ByteArray(16)
        SecureRandom().nextBytes(entropy)
        val words = MnemonicCode.INSTANCE.toMnemonic(entropy)
        prefs.edit().putString(KEY_SEED_MNEMONIC, words.joinToString(" ")).apply()
        return words
    }

    fun getMnemonicOnce(): List<String>? {
        val raw = prefs.getString(KEY_SEED_MNEMONIC, null) ?: return null
        return raw.split(" ")
    }

    companion object {
        private const val PREFS_NAME = "vault_secure_prefs"
        private const val KEY_SEED_MNEMONIC = "seed_mnemonic_encrypted"
    }
}


