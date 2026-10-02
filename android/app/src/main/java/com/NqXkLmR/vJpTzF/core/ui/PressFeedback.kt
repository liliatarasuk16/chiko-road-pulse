package com.NqXkLmR.vJpTzF.core.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

object PressFeedback {

    @SuppressLint("ClickableViewAccessibility")
    fun attach(view: View) {
        view.setOnTouchListener { target, event ->
            try {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> target.animate()
                        .scaleX(PRESSED_SCALE)
                        .scaleY(PRESSED_SCALE)
                        .setDuration(PRESS_MS)
                        .setInterpolator(DecelerateInterpolator())
                        .start()

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> target.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(RELEASE_MS)
                        .setInterpolator(OvershootInterpolator(3f))
                        .start()
                }
            } catch (e: Exception) {
            }
            false
        }
    }

    fun detach(view: View) {
        try {
            view.setOnTouchListener(null)
            view.animate().cancel()
            view.scaleX = 1f
            view.scaleY = 1f
        } catch (e: Exception) {
        }
    }

    private const val PRESSED_SCALE = 0.96f
    private const val PRESS_MS = 80L
    private const val RELEASE_MS = 140L
}
