package com.NqXkLmR.vJpTzF.domain.repository

import com.NqXkLmR.vJpTzF.domain.model.PlayerProfile
import com.NqXkLmR.vJpTzF.domain.model.RunResult

interface ProfileRepository {
    fun load(): PlayerProfile
    fun saveRun(result: RunResult): PlayerProfile
    fun selectShift(shiftId: String)
    fun markTutorialSeen()
}
