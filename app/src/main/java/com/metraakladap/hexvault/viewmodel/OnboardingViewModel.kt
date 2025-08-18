package com.metraakladap.hexvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metraakladap.hexvault.crypto.SeedManager
import com.metraakladap.hexvault.data.OnboardingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val seedManager: SeedManager
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun ensureSeed() {
        viewModelScope.launch {
            if (!seedManager.hasSeed()) {
                val words = seedManager.generateAndStoreMnemonic()
                _state.value = OnboardingState(
                    mnemonicWords = words,
                    isSeedCreated = true
                )
            } else {
                _state.value = OnboardingState(
                    mnemonicWords = seedManager.getMnemonicOnce().orEmpty(),
                    isSeedCreated = true
                )
            }
        }
    }
}


