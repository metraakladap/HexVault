package com.metraakladap.hexvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metraakladap.hexvault.data.LoadingScreenState
import com.metraakladap.hexvault.data.MainState
import com.metraakladap.hexvault.crypto.SeedManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingScreenViewModel @Inject constructor(
    private val seedManager: SeedManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoadingScreenState())
    val state = _state.asStateFlow()

    fun shouldOnboard(): Boolean = !seedManager.hasSeed()

    fun initLoad(onDecide: (Boolean) -> Unit) {
        viewModelScope.launch {
            onDecide(shouldOnboard())
        }
    }
}