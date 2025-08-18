package com.metraakladap.hexvault.viewmodel

import androidx.lifecycle.ViewModel
import com.metraakladap.hexvault.data.LoadingScreenState
import com.metraakladap.hexvault.data.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoadingScreenViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoadingScreenState())
    val state = _state.asStateFlow()
}