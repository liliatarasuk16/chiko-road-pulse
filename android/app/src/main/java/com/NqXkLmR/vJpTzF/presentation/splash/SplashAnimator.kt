package com.NqXkLmR.vJpTzF.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

class SplashAnimator {

    private val animators = ArrayList<Animator>()
    private val animatedViews = ArrayList<View>()

    fun playBadgeEntrance(badge: View) {
        animatedViews.add(badge)
        badge.scaleX = ENTRANCE_SCALE
        badge.scaleY = ENTRANCE_SCALE
        badge.alpha = 0f
        val scaleX = ObjectAnimator.ofFloat(badge, View.SCALE_X, ENTRANCE_SCALE, 1f)
        val scaleY = ObjectAnimator.ofFloat(badge, View.SCALE_Y, ENTRANCE_SCALE, 1f)
        val fade = ObjectAnimator.ofFloat(badge, View.ALPHA, 0f, 1f)
        val entrance = AnimatorSet()
        entrance.playTogether(scaleX, scaleY, fade)
        entrance.duration = BADGE_ENTRANCE_MS
        entrance.interpolator = OvershootInterpolator(2.2f)
        register(entrance)
        entrance.start()
    }

    fun playBadgePulse(badge: View) {
        animatedViews.add(badge)
        val pulseX = ObjectAnimator.ofFloat(badge, View.SCALE_X, 1f, PULSE_SCALE)
        val pulseY = ObjectAnimator.ofFloat(badge, View.SCALE_Y, 1f, PULSE_SCALE)
        listOf(pulseX, pulseY).forEach { animator ->
            animator.duration = PULSE_MS
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.startDelay = BADGE_ENTRANCE_MS
        }
        val pulse = AnimatorSet()
        pulse.playTogether(pulseX, pulseY)
        register(pulse)
        pulse.start()
    }

    fun playTitleEntrance(vararg views: View) {
        views.forEachIndexed { index, view ->
            animatedViews.add(view)
            view.alpha = 0f
            view.translationY = TITLE_OFFSET_DP * view.resources.displayMetrics.density
            val fade = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
            val rise = ObjectAnimator.ofFloat(
                view,
                View.TRANSLATION_Y,
                TITLE_OFFSET_DP * view.resources.displayMetrics.density,
                0f
            )
            val entrance = AnimatorSet()
            entrance.playTogether(fade, rise)
            entrance.duration = TITLE_ENTRANCE_MS
            entrance.startDelay = TITLE_BASE_DELAY_MS + index * TITLE_STAGGER_MS
            entrance.interpolator = DecelerateInterpolator()
            register(entrance)
            entrance.start()
        }
    }

    fun playStripeLoop(stripes: List<View>) {
        stripes.forEachIndexed { index, stripe ->
            animatedViews.add(stripe)
            val distance = STRIPE_TRAVEL_DP * stripe.resources.displayMetrics.density
            val drift = ObjectAnimator.ofFloat(stripe, View.TRANSLATION_Y, -distance, distance)
            drift.duration = STRIPE_LOOP_MS
            drift.repeatCount = ValueAnimator.INFINITE
            drift.repeatMode = ValueAnimator.RESTART
            drift.interpolator = LinearInterpolator()
            drift.startDelay = index * STRIPE_STAGGER_MS
            register(drift)
            drift.start()
        }
    }

    fun playRuleReveal(rule: View) {
        animatedViews.add(rule)
        rule.scaleX = 0f
        val reveal = ObjectAnimator.ofFloat(rule, View.SCALE_X, 0f, 1f)
        reveal.duration = RULE_REVEAL_MS
        reveal.startDelay = RULE_DELAY_MS
        reveal.interpolator = DecelerateInterpolator()
        register(reveal)
        reveal.start()
    }

    fun cancel() {
        animators.forEach { animator ->
            try {
                animator.cancel()
            } catch (e: Exception) {
            }
        }
        animators.clear()
        animatedViews.forEach { view ->
            try {
                view.animate().cancel()
                view.clearAnimation()
            } catch (e: Exception) {
            }
        }
        animatedViews.clear()
    }

    private fun register(animator: Animator) {
        animators.add(animator)
    }

    private companion object {
        const val ENTRANCE_SCALE = 0.82f
        const val PULSE_SCALE = 1.04f
        const val BADGE_ENTRANCE_MS = 520L
        const val PULSE_MS = 900L
        const val TITLE_OFFSET_DP = 24f
        const val TITLE_ENTRANCE_MS = 420L
        const val TITLE_BASE_DELAY_MS = 160L
        const val TITLE_STAGGER_MS = 90L
        const val STRIPE_TRAVEL_DP = 120f
        const val STRIPE_LOOP_MS = 1400L
        const val STRIPE_STAGGER_MS = 220L
        const val RULE_REVEAL_MS = 380L
        const val RULE_DELAY_MS = 420L
    }
}
