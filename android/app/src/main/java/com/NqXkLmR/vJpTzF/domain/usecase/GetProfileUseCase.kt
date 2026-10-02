package com.NqXkLmR.vJpTzF.domain.usecase

import com.NqXkLmR.vJpTzF.domain.model.PlayerProfile
import com.NqXkLmR.vJpTzF.domain.repository.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {

    operator fun invoke(): PlayerProfile = profileRepository.load()
}
