package com.NqXkLmR.vJpTzF.presentation.menu

data class MenuUiState(
    val bestChain: Int = 0,
    val bestAccuracy: Int = 0,
    val totalPoints: String = "0",
    val runsPlayed: Int = 0,
    val shiftId: String = "day",
    val shiftKey: String = "day",
    val tutorialSeen: Boolean = false
)
