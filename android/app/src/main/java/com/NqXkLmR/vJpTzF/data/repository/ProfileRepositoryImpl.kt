package com.NqXkLmR.vJpTzF.data.repository

import com.NqXkLmR.vJpTzF.data.local.PreferencesStorage
import com.NqXkLmR.vJpTzF.domain.model.PlayerProfile
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val storage: PreferencesStorage) : ProfileRepository {

    override fun load(): PlayerProfile = PlayerProfile(
        bestChain = storage.bestChain,
        bestAccuracy = storage.bestAccuracy,
        totalPoints = storage.totalPoints,
        runsPlayed = storage.runsPlayed,
        selectedShiftId = storage.selectedShiftId,
        tutorialSeen = storage.tutorialSeen
    )

    override fun saveRun(result: RunResult): PlayerProfile {
        if (result.chain > storage.bestChain) {
            storage.bestChain = result.chain
        }
        if (result.accuracy > storage.bestAccuracy) {
            storage.bestAccuracy = result.accuracy
        }
        storage.totalPoints = storage.totalPoints + result.points
        storage.runsPlayed = storage.runsPlayed + 1
        return load()
    }

    override fun selectShift(shiftId: String) {
        storage.selectedShiftId = shiftId
    }

    override fun markTutorialSeen() {
        storage.tutorialSeen = true
    }
}
