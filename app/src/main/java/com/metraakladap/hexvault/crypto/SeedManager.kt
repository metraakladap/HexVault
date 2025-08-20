package com.metraakladap.hexvault.crypto

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.bitcoinj.crypto.MnemonicCode
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import java.security.GeneralSecurityException

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

    fun hasSeed(): Boolean = try {
        prefs.contains(KEY_SEED_MNEMONIC)
    } catch (e: AEADBadTagException) {
        resetCorruptedStore()
        false
    } catch (e: GeneralSecurityException) {
        resetCorruptedStore()
        false
    }

    fun generateAndStoreMnemonic(): List<String> {
        check(!hasSeed()) { "Seed already exists" }
        val entropy = ByteArray(16)
        SecureRandom().nextBytes(entropy)
        val words = MnemonicCode.INSTANCE.toMnemonic(entropy)
        prefs.edit().putString(KEY_SEED_MNEMONIC, words.joinToString(" ")).apply()
        return words
    }

    fun getMnemonicOnce(): List<String>? = try {
        val raw = prefs.getString(KEY_SEED_MNEMONIC, null) ?: return null
        raw.split(" ")
    } catch (e: AEADBadTagException) {
        resetCorruptedStore()
        null
    } catch (e: GeneralSecurityException) {
        resetCorruptedStore()
        null
    }

    private fun resetCorruptedStore() {
        // If encrypted prefs were restored from backup without matching keystore key,
        // wipe the stored file to avoid constant crashes and force re-onboarding.
        appContext.deleteSharedPreferences(PREFS_NAME)
    }

    companion object {
        private const val PREFS_NAME = "vault_secure_prefs"
        private const val KEY_SEED_MNEMONIC = "seed_mnemonic_encrypted"
    }
}


