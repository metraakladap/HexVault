package com.metraakladap.hexvault.data

data class OnboardingState(
    val mnemonicWords: List<String> = emptyList(),
    val isSeedCreated: Boolean = false
)


