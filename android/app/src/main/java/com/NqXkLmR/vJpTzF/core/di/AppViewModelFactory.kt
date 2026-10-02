package com.NqXkLmR.vJpTzF.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.NqXkLmR.vJpTzF.presentation.game.GameViewModel
import com.NqXkLmR.vJpTzF.presentation.gameover.GameOverViewModel
import com.NqXkLmR.vJpTzF.presentation.menu.MenuViewModel
import com.NqXkLmR.vJpTzF.presentation.splash.SplashViewModel

class AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.getProfileUseCase,
                ServiceLocator.shiftRepository,
                ServiceLocator.profileRepository,
                ServiceLocator.formatPointsUseCase
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.getProfileUseCase,
                ServiceLocator.shiftRepository,
                ServiceLocator.generateSequenceUseCase,
                ServiceLocator.spawnPlanUseCase,
                ServiceLocator.evaluatePickUseCase
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.saveRunResultUseCase,
                ServiceLocator.getProfileUseCase,
                ServiceLocator.formatPointsUseCase
            )
            else -> throw IllegalArgumentException("Unknown ViewModel " + modelClass.name)
        }
        return created as T
    }
}
