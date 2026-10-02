package com.NqXkLmR.vJpTzF.domain.usecase

import com.NqXkLmR.vJpTzF.domain.model.PlayerProfile
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.domain.repository.ProfileRepository

class SaveRunResultUseCase(private val profileRepository: ProfileRepository) {

    operator fun invoke(result: RunResult): PlayerProfile = profileRepository.saveRun(result)
}
