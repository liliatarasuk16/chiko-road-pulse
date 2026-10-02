package com.NqXkLmR.vJpTzF.domain.repository

import com.NqXkLmR.vJpTzF.domain.model.ShiftPreset

interface ShiftRepository {
    fun presets(): List<ShiftPreset>
    fun byId(shiftId: String): ShiftPreset
}
