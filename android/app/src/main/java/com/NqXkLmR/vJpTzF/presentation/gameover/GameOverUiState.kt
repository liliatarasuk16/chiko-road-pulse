package com.NqXkLmR.vJpTzF.presentation.gameover

import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.domain.model.FinishReason

data class GameOverUiState(
    val isWin: Boolean = false,
    val reason: FinishReason = FinishReason.TIME_UP,
    val chain: Int = 0,
    val chainTarget: Int = GameConfig.CHAIN_TARGET,
    val accuracy: Int = 0,
    val bestChain: Int = 0,
    val points: String = "0",
    val isNewBest: Boolean = false
)
