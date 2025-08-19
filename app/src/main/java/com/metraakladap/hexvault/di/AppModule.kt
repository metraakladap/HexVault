package com.metraakladap.hexvault.di

import android.content.Context
import com.metraakladap.hexvault.crypto.SeedManager
import com.metraakladap.hexvault.crypto.WalletManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSeedManager(@ApplicationContext context: Context): SeedManager =
        SeedManager(context)

    @Provides
    @Singleton
    fun provideWalletManager(seedManager: SeedManager): WalletManager =
        WalletManager(seedManager)
}


