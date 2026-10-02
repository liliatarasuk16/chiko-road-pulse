package com.NqXkLmR.vJpTzF.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.NqXkLmR.vJpTzF.core.config.GameConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val mutableState = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = mutableState.asStateFlow()

    private var timerStarted = false

    fun startTimer() {
        if (timerStarted) {
            return
        }
        timerStarted = true
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            mutableState.value = SplashUiState(isLoading = false, navigateToMenu = true)
        }
    }

    fun onNavigationHandled() {
        mutableState.value = mutableState.value.copy(navigateToMenu = false)
    }
}
