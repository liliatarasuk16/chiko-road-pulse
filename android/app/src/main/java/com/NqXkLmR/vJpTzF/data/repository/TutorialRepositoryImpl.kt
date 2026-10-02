package com.NqXkLmR.vJpTzF.data.repository

import com.NqXkLmR.vJpTzF.data.sample.SampleData
import com.NqXkLmR.vJpTzF.domain.model.TutorialStep
import com.NqXkLmR.vJpTzF.domain.repository.TutorialRepository

class TutorialRepositoryImpl : TutorialRepository {

    override fun steps(): List<TutorialStep> = SampleData.tutorialSteps
}
