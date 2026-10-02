package com.NqXkLmR.vJpTzF.data.repository

import com.NqXkLmR.vJpTzF.data.sample.SampleData
import com.NqXkLmR.vJpTzF.domain.model.ShiftPreset
import com.NqXkLmR.vJpTzF.domain.repository.ShiftRepository

class ShiftRepositoryImpl : ShiftRepository {

    override fun presets(): List<ShiftPreset> = SampleData.shiftPresets

    override fun byId(shiftId: String): ShiftPreset =
        SampleData.shiftPresets.firstOrNull { it.id == shiftId } ?: SampleData.shiftPresets.first()
}
