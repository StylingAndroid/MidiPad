package com.stylingandroid.midipad.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.stylingandroid.midipad.R
import com.stylingandroid.midipad.getColour

class PadView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        private val bounds: RectF = RectF(),
        private val outlinePath: Path = Path()
) : View(context, attrs, defStyleAttr) {
    private var padColour: Int = 0
    private var outlineColour: Int = 0
    private var outlineWidth: Float = 1f
    private var cornerRadius: Float = 0f
    private var fadeInDuration: Long = 0
    private var fadeOutDuration: Long = 0

    private var animator: Animator? = null

    private var pressure: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    private val outline: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            color = outlineColour
            strokeWidth = outlineWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private val fill: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            color = padColour
            style = Paint.Style.FILL
        }
    }

    init {
        if (!isInEditMode) {
            attrs?.apply {
                context.obtainStyledAttributes(this, R.styleable.PadView)
                        .apply {
                            val defaultColour = context.theme.getColour(R.attr.colorAccent)
                            padColour = getColor(R.styleable.PadView_padColour, defaultColour)
                            outlineColour = getColor(R.styleable.PadView_outlineColour, defaultColour)
                            outlineWidth = getDimension(R.styleable.PadView_outlineWidth, 1f)
                            cornerRadius = getFloat(R.styleable.PadView_cornerRadius, 0f)
                            fadeInDuration = getLong(R.styleable.PadView_fadeInDuration, 0)
                            fadeOutDuration = getLong(R.styleable.PadView_fadeOutDuration, 0)
                        }
                        .apply {
                            recycle()
                        }
            }
        }
    }

    private fun TypedArray.getLong(index: Int, default: Int) =
        getInt(index, default).toLong()

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) =
            super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight).run {
                adjustBounds(newWidth.toFloat(), newHeight.toFloat())
            }

    private fun adjustBounds(width: Float, height: Float) {
        bounds.set(0f, 0f, width, height)
        outlinePath.apply {
            reset()
            addRoundRect(bounds, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        fill.shader = RadialGradient(
                width / 2,
                height / 2,
                Math.abs(width - height),
                padColour,
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
        )
        setLayerType(View.LAYER_TYPE_SOFTWARE, fill)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            super.onDraw(this)
            drawPath(outlinePath, outline)
            save().also {
                clipPath(outlinePath)
                translate(-pressureOffset(bounds.width()), -pressureOffset(bounds.height()))
                scale(pressure, pressure)
                drawRect(bounds, fill)
                restoreToCount(it)
            }
        }
    }

    @Suppress("UnnecessaryParentheses")
    private fun pressureOffset(dimension: Float) = ((pressure * dimension) - dimension) / 2

    override fun onTouchEvent(event: MotionEvent?): Boolean =
            event?.action?.let {
                when (it) {
                    MotionEvent.ACTION_DOWN -> {
                        animatePressure(1f + event.pressure, fadeInDuration)
                        performClick()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        animatePressure(1f, fadeOutDuration)
                        performClick()
                        true
                    }
                    else -> super.onTouchEvent(event)
                }
            } ?: super.onTouchEvent(event)

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun animatePressure(newPressure: Float, duration: Long) {
        animator?.takeIf { it.isRunning }?.cancel()
        with(ObjectAnimator.ofFloat(this, PRESSURE, pressure, newPressure)) {
            animator = this
            this.duration = duration
            start()
        }
    }

    companion object {
        private const val PRESSURE = "pressure"
    }
}
