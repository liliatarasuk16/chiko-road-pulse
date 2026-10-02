package com.NqXkLmR.vJpTzF.data.sample

import com.NqXkLmR.vJpTzF.domain.model.ShiftPreset
import com.NqXkLmR.vJpTzF.domain.model.TutorialStep

object SampleData {

    const val DEFAULT_SHIFT_ID = "day"

    val shiftPresets: List<ShiftPreset> = listOf(
        ShiftPreset(
            id = "day",
            marker = "D",
            titleKey = "day",
            shortKey = "day",
            speedDpPerSecond = 185,
            spawnIntervalMs = 900L,
            chainTarget = 30,
            roundDurationMs = 90_000L,
            reward = 100
        ),
        ShiftPreset(
            id = "rush",
            marker = "R",
            titleKey = "rush",
            shortKey = "rush",
            speedDpPerSecond = 245,
            spawnIntervalMs = 760L,
            chainTarget = 30,
            roundDurationMs = 80_000L,
            reward = 160
        ),
        ShiftPreset(
            id = "night",
            marker = "N",
            titleKey = "night",
            shortKey = "night",
            speedDpPerSecond = 310,
            spawnIntervalMs = 640L,
            chainTarget = 30,
            roundDurationMs = 70_000L,
            reward = 240
        )
    )

    val tutorialSteps: List<TutorialStep> = listOf(
        TutorialStep(1, "step_one", "cart"),
        TutorialStep(2, "step_two", "parcel_amber"),
        TutorialStep(3, "step_three", "parcel_cyan"),
        TutorialStep(4, "step_four", "crate_empty")
    )
}
