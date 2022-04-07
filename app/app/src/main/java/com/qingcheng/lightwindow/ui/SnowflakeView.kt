package com.qingcheng.lightwindow.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.random.Random

class SnowflakeView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val mPaint = Paint()
    private val mList = mutableListOf<Snow>()

    data class Snow(
        val position: P,
        val origin: P,
        val color: Int,
        val speed: Int,
        val radius: Float,
    )

    data class P(var x: Float, var y: Float)

    private val mDefaultWidth = dp2px(100)
    private val mDefaultHeight = dp2px(100)
    private val num = 0.1f
    private var mMeasureWidth = 0
    private var mMeasureHeight = 0

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mMeasureWidth = widthSpecSize
        } else {
            mMeasureWidth = mDefaultWidth
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                mMeasureWidth = min(mMeasureWidth, widthSpecSize)
            }
        }

        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mMeasureHeight = heightSpecSize
        } else {
            mMeasureHeight = mDefaultHeight
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                mMeasureHeight = min(mMeasureHeight, heightSpecSize)
            }
        }
        mMeasureHeight = mMeasureHeight - paddingBottom - paddingTop
        mMeasureWidth = mMeasureWidth - paddingLeft - paddingBottom
        setMeasuredDimension(mMeasureWidth, mMeasureHeight)

        for (i in 0 until (mMeasureWidth * num).toInt()) {

            val x = (0..mMeasureWidth).random().toFloat()
            val y = (0..mMeasureHeight).random().toFloat()

            val position = P(x, y)
            val origin = P(x, 0f)
            val radius = Random.nextFloat() * 3 + dp2px(1)
            val speed = 1 + (0..3).random()
            val color = Color.argb((0..255).random(), 255, 255, 255)
            val lBobbleBean = Snow(position, origin, color, speed, radius)
            mList.add(lBobbleBean)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (snow in mList) {
            val position = snow.position
            position.y += snow.speed
            position.x += (-1..1).random()*0.5f
            if (position.y > mMeasureHeight) position.y = 0f
            if (position.x > mMeasureWidth) position.x -= mMeasureWidth
            if (position.x < 0) position.x += mMeasureWidth
            mPaint.color = snow.color
            canvas.drawCircle(
                snow.position.x,
                snow.position.y,
                snow.radius,
                mPaint
            )
        }

        postInvalidateDelayed(16L)
    }

    private fun dp2px(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}
