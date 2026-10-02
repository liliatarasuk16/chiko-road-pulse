package com.NqXkLmR.vJpTzF.domain.repository

import com.NqXkLmR.vJpTzF.domain.model.TutorialStep

interface TutorialRepository {
    fun steps(): List<TutorialStep>
}
