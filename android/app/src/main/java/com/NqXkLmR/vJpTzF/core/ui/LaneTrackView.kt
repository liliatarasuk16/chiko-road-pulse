package com.NqXkLmR.vJpTzF.core.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.domain.model.Lane
import com.NqXkLmR.vJpTzF.domain.model.RoadItem
import com.NqXkLmR.vJpTzF.domain.model.RoadItemKind
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

class LaneTrackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val density = resources.displayMetrics.density
    private val framePx = ((GameConfig.BOARD_PAD_DP + GameConfig.BOARD_BORDER_DP) * density).toInt()
    private val borderPx = GameConfig.BOARD_BORDER_DP * density
    private val reservedBottomPx = GameConfig.BOARD_RESERVED_BOTTOM_DP * density

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.track_base)
    }

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = borderPx
        color = ContextCompat.getColor(context, R.color.accent_amber)
    }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
        color = ContextCompat.getColor(context, R.color.lane_divider)
        pathEffect = DashPathEffect(floatArrayOf(10f * density, 10f * density), 0f)
    }

    private val defaultFrameColor = ContextCompat.getColor(context, R.color.accent_amber)

    private val spriteCache = HashMap<Int, Drawable>()

    private var laneWidth = 0f
    private var boardWidth = 0
    private var items: List<RoadItem> = emptyList()
    private var cartLane: Lane = Lane.CENTER
    private var cartX = -1f
    private var flashColor = defaultFrameColor
    private var flashUntil = 0L
    private var shakeUntil = 0L
    private var blinkUntil = 0L

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxWidth = min(availableWidth, (GameConfig.BOARD_MAX_W_DP * density).toInt())
        val laneSpan = (maxWidth - 2 * framePx) / GameConfig.LANE_COUNT
        laneWidth = laneSpan.toFloat()
        boardWidth = laneSpan * GameConfig.LANE_COUNT + 2 * framePx
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(boardWidth, height)
    }

    fun render(nextItems: List<RoadItem>, lane: Lane) {
        items = nextItems
        cartLane = lane
        val target = laneCenterX(lane)
        cartX = if (cartX < 0f) target else cartX + (target - cartX) * CART_LERP
        invalidate()
    }

    fun flashFrame(@ColorInt color: Int) {
        flashColor = color
        flashUntil = SystemClock.uptimeMillis() + GameConfig.FEEDBACK_FLASH_MS
        invalidate()
    }

    fun shakeCart() {
        shakeUntil = SystemClock.uptimeMillis() + SHAKE_DURATION_MS
        invalidate()
    }

    fun blinkCart() {
        blinkUntil = SystemClock.uptimeMillis() + GameConfig.INVULNERABLE_MS
        invalidate()
    }

    fun resetEffects() {
        flashUntil = 0L
        shakeUntil = 0L
        blinkUntil = 0L
        cartX = -1f
        items = emptyList()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (boardWidth <= 0 || height <= 0) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val inset = borderPx / 2f
        canvas.drawRect(inset, inset, boardWidth - inset, height - inset, backgroundPaint)
        framePaint.color = if (now < flashUntil) flashColor else defaultFrameColor
        canvas.drawRect(inset, inset, boardWidth - inset, height - inset, framePaint)

        for (index in 1 until GameConfig.LANE_COUNT) {
            val x = framePx + laneWidth * index
            canvas.drawLine(x, framePx.toFloat(), x, height - framePx.toFloat(), dividerPaint)
        }

        val itemSize = min(laneWidth - 10f * density, GameConfig.ROAD_ITEM_DP * density)
        val pickY = height - framePx - reservedBottomPx
        val span = pickY + itemSize / 2f

        for (item in items) {
            val drawableRes = if (item.kind == RoadItemKind.CRATE) {
                R.drawable.sprite_crate_empty
            } else {
                item.color?.let { ParcelPalette.spriteRes(it) } ?: R.drawable.sprite_crate_empty
            }
            val centerY = -itemSize / 2f + item.progress / GameConfig.PICK_PROGRESS * span
            drawSprite(canvas, drawableRes, laneCenterX(item.lane), centerY, itemSize, 255)
        }

        val cartSize = min(laneWidth - 6f * density, CART_MAX_DP * density)
        val shakeOffset = if (now < shakeUntil) {
            val phase = (shakeUntil - now).toFloat() / SHAKE_DURATION_MS
            sin(phase * SHAKE_CYCLES) * SHAKE_AMPLITUDE_DP * density
        } else {
            0f
        }
        val cartAlpha = if (now < blinkUntil) {
            val pulse = abs(sin((blinkUntil - now) / BLINK_PERIOD))
            (120 + 135 * pulse).toInt()
        } else {
            255
        }
        val currentX = if (cartX < 0f) laneCenterX(cartLane) else cartX
        drawSprite(canvas, R.drawable.sprite_cart, currentX + shakeOffset, pickY, cartSize, cartAlpha)

        if (now < flashUntil || now < shakeUntil || now < blinkUntil) {
            invalidate()
        }
    }

    private fun drawSprite(
        canvas: Canvas,
        @DrawableRes resId: Int,
        centerX: Float,
        centerY: Float,
        size: Float,
        alpha: Int
    ) {
        val drawable = spriteCache.getOrPut(resId) {
            ContextCompat.getDrawable(context, resId)?.mutate() ?: return
        }
        val half = (size / 2f).toInt()
        drawable.alpha = alpha
        drawable.setBounds(
            centerX.toInt() - half,
            centerY.toInt() - half,
            centerX.toInt() + half,
            centerY.toInt() + half
        )
        drawable.draw(canvas)
    }

    private fun laneCenterX(lane: Lane): Float = framePx + laneWidth * (lane.index + 0.5f)

    private companion object {
        const val CART_LERP = 0.28f
        const val CART_MAX_DP = 84f
        const val SHAKE_DURATION_MS = 180L
        const val SHAKE_CYCLES = 18f
        const val SHAKE_AMPLITUDE_DP = 8f
        const val BLINK_PERIOD = 60.0
    }
}
