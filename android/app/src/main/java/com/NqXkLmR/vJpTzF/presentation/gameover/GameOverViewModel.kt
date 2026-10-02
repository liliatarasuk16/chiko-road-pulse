package com.NqXkLmR.vJpTzF.presentation.gameover

import androidx.lifecycle.ViewModel
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.domain.usecase.FormatPointsUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GetProfileUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.SaveRunResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val saveRunResult: SaveRunResultUseCase,
    private val getProfile: GetProfileUseCase,
    private val formatPoints: FormatPointsUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = mutableState.asStateFlow()

    private var submitted = false

    fun submit(result: RunResult) {
        if (submitted) {
            return
        }
        submitted = true
        val before = getProfile()
        val after = saveRunResult(result)
        mutableState.value = GameOverUiState(
            isWin = result.isWin,
            reason = result.reason,
            chain = result.chain,
            chainTarget = result.chainTarget,
            accuracy = result.accuracy,
            bestChain = after.bestChain,
            points = formatPoints(result.points),
            isNewBest = result.chain > before.bestChain && result.chain > 0
        )
    }
}
