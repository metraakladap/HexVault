package com.metraakladap.hexvault.data

data class SettingsState(
    val currentLocaleTag: String = "en",
    val supportedLocales: List<String> = listOf("en", "uk", "pl")
)


