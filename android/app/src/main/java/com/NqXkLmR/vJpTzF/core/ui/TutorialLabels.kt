package com.NqXkLmR.vJpTzF.core.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.NqXkLmR.vJpTzF.R

object TutorialLabels {

    @StringRes
    fun textRes(key: String): Int = when (key) {
        "step_two" -> R.string.tutorial_step_two
        "step_three" -> R.string.tutorial_step_three
        "step_four" -> R.string.tutorial_step_four
        else -> R.string.tutorial_step_one
    }

    @DrawableRes
    fun spriteRes(key: String): Int = when (key) {
        "parcel_amber" -> R.drawable.sprite_parcel_amber
        "parcel_cyan" -> R.drawable.sprite_parcel_cyan
        "crate_empty" -> R.drawable.sprite_crate_empty
        else -> R.drawable.sprite_cart
    }
}
