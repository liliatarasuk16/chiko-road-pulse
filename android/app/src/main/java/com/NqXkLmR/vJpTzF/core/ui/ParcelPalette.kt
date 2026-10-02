package com.NqXkLmR.vJpTzF.core.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor

object ParcelPalette {

    @ColorRes
    fun colorRes(color: ParcelColor): Int = when (color) {
        ParcelColor.AMBER -> R.color.accent_amber
        ParcelColor.CORAL -> R.color.accent_coral
        ParcelColor.CYAN -> R.color.accent_cyan
        ParcelColor.LIME -> R.color.accent_lime
    }

    @DrawableRes
    fun spriteRes(color: ParcelColor): Int = when (color) {
        ParcelColor.AMBER -> R.drawable.sprite_parcel_amber
        ParcelColor.CORAL -> R.drawable.sprite_parcel_coral
        ParcelColor.CYAN -> R.drawable.sprite_parcel_cyan
        ParcelColor.LIME -> R.drawable.sprite_parcel_lime
    }
}
