package com.NqXkLmR.vJpTzF.domain.model

data class RunResult(
    val chain: Int,
    val chainTarget: Int,
    val correctPicks: Int,
    val wrongPicks: Int,
    val crashes: Int,
    val accuracy: Int,
    val points: Int,
    val isWin: Boolean,
    val reason: FinishReason
)
