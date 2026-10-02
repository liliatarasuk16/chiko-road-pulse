package com.NqXkLmR.vJpTzF.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.databinding.ViewHudBarBinding

class HudBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewHudBarBinding.inflate(LayoutInflater.from(context), this)

    private val pips: List<View> = listOf(
        binding.hudLife0,
        binding.hudLife1,
        binding.hudLife2
    )

    fun setLaneListeners(onLeft: () -> Unit, onRight: () -> Unit) {
        binding.hudButtonLeft.setOnClickListener { onLeft() }
        binding.hudButtonRight.setOnClickListener { onRight() }
    }

    fun render(crashes: Int, wrongPicks: Int) {
        val livesLeft = (GameConfig.MAX_CRASHES - crashes).coerceAtLeast(0)
        pips.forEachIndexed { index, pip ->
            pip.setBackgroundResource(
                if (index < livesLeft) R.drawable.life_pip_on else R.drawable.life_pip_off
            )
        }
        ViewCompat.setStateDescription(
            binding.hudLifeRow,
            context.getString(R.string.game_lives_state, livesLeft, GameConfig.MAX_CRASHES)
        )
        binding.hudMiss.text =
            context.getString(R.string.game_miss_counter, wrongPicks, GameConfig.MAX_WRONG_PICKS)
    }

    fun setControlsEnabled(enabled: Boolean) {
        binding.hudButtonLeft.isEnabled = enabled
        binding.hudButtonRight.isEnabled = enabled
        val targetAlpha = if (enabled) 1f else 0.45f
        binding.hudButtonLeft.alpha = targetAlpha
        binding.hudButtonRight.alpha = targetAlpha
    }
}
