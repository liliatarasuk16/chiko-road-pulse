package com.NqXkLmR.vJpTzF.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.databinding.ViewSequenceStripBinding
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor

class SequenceStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSequenceStripBinding.inflate(LayoutInflater.from(context), this)

    private val cells: List<View> = listOf(
        binding.sequenceCell0,
        binding.sequenceCell1,
        binding.sequenceCell2,
        binding.sequenceCell3,
        binding.sequenceCell4,
        binding.sequenceCell5,
        binding.sequenceCell6
    )

    fun render(sequence: List<ParcelColor>, currentIndex: Int) {
        cells.forEachIndexed { offset, cell ->
            val sequenceIndex = currentIndex + offset
            val color = sequence.getOrNull(sequenceIndex)
            if (color == null) {
                cell.visibility = View.INVISIBLE
                return@forEachIndexed
            }
            cell.visibility = View.VISIBLE
            cell.setBackgroundResource(
                if (offset == 0) R.drawable.sequence_cell_active else R.drawable.sequence_cell
            )
            cell.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, ParcelPalette.colorRes(color))
            )
            val scale = if (offset == 0) ACTIVE_SCALE else 1f
            cell.scaleX = scale
            cell.scaleY = scale
            cell.alpha = if (offset == 0) 1f else 0.82f - offset * 0.06f
        }
    }

    fun shiftOut() {
        val leading = cells.firstOrNull() ?: return
        leading.translationX = 0f
        leading.alpha = 1f
        leading.animate()
            .translationX(-leading.width.toFloat())
            .alpha(0f)
            .setDuration(SHIFT_DURATION_MS)
            .withEndAction {
                try {
                    if (!leading.isAttachedToWindow) {
                        return@withEndAction
                    }
                    leading.translationX = 0f
                    leading.alpha = 1f
                } catch (e: Exception) {
                }
            }
            .start()
    }

    fun cancelAnimations() {
        cells.forEach { cell ->
            cell.animate().cancel()
            cell.translationX = 0f
            cell.alpha = 1f
        }
    }

    private companion object {
        const val ACTIVE_SCALE = 1.25f
        const val SHIFT_DURATION_MS = 180L
    }
}
