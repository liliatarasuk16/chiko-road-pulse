package com.NqXkLmR.vJpTzF.domain.model

data class ShiftPreset(
    val id: String,
    val marker: String,
    val titleKey: String,
    val shortKey: String,
    val speedDpPerSecond: Int,
    val spawnIntervalMs: Long,
    val chainTarget: Int,
    val roundDurationMs: Long,
    val reward: Int
) {
    val speedFactor: Float
        get() = speedDpPerSecond / 185f
}
