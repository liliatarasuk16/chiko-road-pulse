package com.NqXkLmR.vJpTzF.core.di

import android.content.Context
import com.NqXkLmR.vJpTzF.data.local.PreferencesStorage
import com.NqXkLmR.vJpTzF.data.repository.ProfileRepositoryImpl
import com.NqXkLmR.vJpTzF.data.repository.ShiftRepositoryImpl
import com.NqXkLmR.vJpTzF.data.repository.TutorialRepositoryImpl
import com.NqXkLmR.vJpTzF.domain.repository.ProfileRepository
import com.NqXkLmR.vJpTzF.domain.repository.ShiftRepository
import com.NqXkLmR.vJpTzF.domain.repository.TutorialRepository
import com.NqXkLmR.vJpTzF.domain.usecase.EvaluatePickUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.FormatPointsUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GenerateSequenceUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GetProfileUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.SaveRunResultUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.SpawnPlanUseCase

object ServiceLocator {

    private var profileRepositoryRef: ProfileRepository? = null
    private var shiftRepositoryRef: ShiftRepository? = null
    private var tutorialRepositoryRef: TutorialRepository? = null

    fun init(context: Context) {
        if (profileRepositoryRef != null) {
            return
        }
        val storage = PreferencesStorage(context.applicationContext)
        profileRepositoryRef = ProfileRepositoryImpl(storage)
        shiftRepositoryRef = ShiftRepositoryImpl()
        tutorialRepositoryRef = TutorialRepositoryImpl()
    }

    val profileRepository: ProfileRepository
        get() = requireNotNull(profileRepositoryRef) { "ServiceLocator is not initialised" }

    val shiftRepository: ShiftRepository
        get() = requireNotNull(shiftRepositoryRef) { "ServiceLocator is not initialised" }

    val tutorialRepository: TutorialRepository
        get() = requireNotNull(tutorialRepositoryRef) { "ServiceLocator is not initialised" }

    val getProfileUseCase: GetProfileUseCase
        get() = GetProfileUseCase(profileRepository)

    val saveRunResultUseCase: SaveRunResultUseCase
        get() = SaveRunResultUseCase(profileRepository)

    val generateSequenceUseCase: GenerateSequenceUseCase
        get() = GenerateSequenceUseCase()

    val spawnPlanUseCase: SpawnPlanUseCase
        get() = SpawnPlanUseCase()

    val evaluatePickUseCase: EvaluatePickUseCase
        get() = EvaluatePickUseCase()

    val formatPointsUseCase: FormatPointsUseCase
        get() = FormatPointsUseCase()
}
