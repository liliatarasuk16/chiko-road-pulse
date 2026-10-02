package com.NqXkLmR.vJpTzF.presentation.menu

import androidx.lifecycle.ViewModel
import com.NqXkLmR.vJpTzF.domain.repository.ProfileRepository
import com.NqXkLmR.vJpTzF.domain.repository.ShiftRepository
import com.NqXkLmR.vJpTzF.domain.usecase.FormatPointsUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val getProfile: GetProfileUseCase,
    private val shiftRepository: ShiftRepository,
    private val profileRepository: ProfileRepository,
    private val formatPoints: FormatPointsUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = mutableState.asStateFlow()

    fun refresh() {
        val profile = getProfile()
        val shift = shiftRepository.byId(profile.selectedShiftId)
        mutableState.value = MenuUiState(
            bestChain = profile.bestChain,
            bestAccuracy = profile.bestAccuracy,
            totalPoints = formatPoints(profile.totalPoints),
            runsPlayed = profile.runsPlayed,
            shiftId = shift.id,
            shiftKey = shift.shortKey,
            tutorialSeen = profile.tutorialSeen
        )
    }

    fun selectShift(shiftId: String) {
        profileRepository.selectShift(shiftId)
        refresh()
    }

    fun markTutorialSeen() {
        profileRepository.markTutorialSeen()
        refresh()
    }
}
