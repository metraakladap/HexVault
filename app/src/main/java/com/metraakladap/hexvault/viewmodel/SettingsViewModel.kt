package com.metraakladap.hexvault.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.metraakladap.hexvault.data.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun setLocale(tag: String) {
        _state.value = _state.value.copy(currentLocaleTag = tag)
        applyLocale(tag)
    }

    private fun applyLocale(tag: String) {
        // Compose UI will read Activity configuration; we can recreate activity after change in the screen
    }
}


