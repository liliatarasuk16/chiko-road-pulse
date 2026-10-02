package com.NqXkLmR.vJpTzF.core.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var compact = false

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
        val value = typed.getString(R.styleable.StatCardView_statValue).orEmpty()
        val label = typed.getString(R.styleable.StatCardView_statLabel).orEmpty()
        val accent = typed.getColor(
            R.styleable.StatCardView_statAccentColor,
            ContextCompat.getColor(context, R.color.accent_amber)
        )
        compact = typed.getBoolean(R.styleable.StatCardView_statCompact, false)
        typed.recycle()
        setValue(value)
        setLabel(label)
        setAccentColor(accent)
        applyCompact()
    }

    fun setValue(value: String) {
        binding.statValue.text = value
        ViewCompat.setStateDescription(this, value)
    }

    fun setLabel(label: String) {
        binding.statLabel.text = label
        contentDescription = label
    }

    fun setAccentColor(@ColorInt color: Int) {
        binding.statAccent.setBackgroundColor(color)
        binding.statValue.setTextColor(color)
    }

    fun setCompact(value: Boolean) {
        compact = value
        applyCompact()
    }

    private fun applyCompact() {
        val valueSize = if (compact) 16f else 22f
        binding.statValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, valueSize)
        binding.statLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (compact) 9f else 11f)
        val horizontal = if (compact) dp(10) else dp(12)
        val vertical = if (compact) dp(6) else dp(10)
        binding.statBody.setPadding(horizontal, vertical, horizontal, vertical)
        binding.statAccent.visibility = if (compact) GONE else VISIBLE
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
