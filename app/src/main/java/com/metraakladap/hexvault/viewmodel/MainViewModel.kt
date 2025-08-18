package com.metraakladap.hexvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metraakladap.hexvault.data.MainState
import com.metraakladap.hexvault.repository.PriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val priceRepository: PriceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun loadPrice() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val price = priceRepository.getBtcUsdPrice()
                _state.value = _state.value.copy(isLoading = false, btcUsdPrice = price)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }
}