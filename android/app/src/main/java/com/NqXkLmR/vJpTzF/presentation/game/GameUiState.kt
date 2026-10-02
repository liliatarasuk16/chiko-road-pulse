package com.NqXkLmR.vJpTzF.presentation.game

import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.domain.model.Lane
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor
import com.NqXkLmR.vJpTzF.domain.model.PickOutcome
import com.NqXkLmR.vJpTzF.domain.model.RoadItem
import com.NqXkLmR.vJpTzF.domain.model.RunResult

data class GameUiState(
    val phase: GamePhase = GamePhase.READY,
    val items: List<RoadItem> = emptyList(),
    val cartLane: Lane = Lane.CENTER,
    val sequence: List<ParcelColor> = emptyList(),
    val chain: Int = 0,
    val chainTarget: Int = GameConfig.CHAIN_TARGET,
    val wrongPicks: Int = 0,
    val crashes: Int = 0,
    val timeLeftMs: Long = 0L,
    val roundDurationMs: Long = 1L,
    val feedbackId: Long = 0L,
    val feedback: PickOutcome? = null,
    val result: RunResult? = null
)
