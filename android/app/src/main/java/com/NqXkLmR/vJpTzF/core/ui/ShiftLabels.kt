package com.NqXkLmR.vJpTzF.core.ui

import androidx.annotation.StringRes
import com.NqXkLmR.vJpTzF.R

object ShiftLabels {

    @StringRes
    fun titleRes(key: String): Int = when (key) {
        "rush" -> R.string.shift_rush_title
        "night" -> R.string.shift_night_title
        else -> R.string.shift_day_title
    }

    @StringRes
    fun shortRes(key: String): Int = when (key) {
        "rush" -> R.string.shift_rush_short
        "night" -> R.string.shift_night_short
        else -> R.string.shift_day_short
    }
}
