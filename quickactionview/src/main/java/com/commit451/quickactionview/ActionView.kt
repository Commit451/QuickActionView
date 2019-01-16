package com.commit451.quickactionview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.view.View

/**
 * View that shows the action
 */
@SuppressLint("ViewConstructor")
class ActionView(
        context: Context,
        val action: Action,
        private val configHelper: ConfigHelper
) : View(context), ValueAnimator.AnimatorUpdateListener {

    private var mBackgroundPaint: Paint? = null

    private var mActionCircleRadius: Int = 0
    internal var actionCircleRadiusExpanded: Float = 0.toFloat()
        private set
    private var shadowOffsetY: Float = 0.toFloat()
    private var mIconPadding: Int = 0
    var interpolation: Float = 0.toFloat()
    private var mCurrentAnimator: ValueAnimator? = null
    private var mSelected = false
    private val mCenter = Point()
    private val mTempPoint = Point()

    private val actionState: IntArray
        get() = if (mSelected) {
            intArrayOf(android.R.attr.state_selected)
        } else {
            intArrayOf()
        }

    private val maxShadowRadius: Float
        get() = actionCircleRadiusExpanded / 5.0f

    private val interpolatedRadius: Float
        get() = mActionCircleRadius + (actionCircleRadiusExpanded - mActionCircleRadius) * interpolation

    private val currentShadowRadius: Float
        get() = interpolatedRadius / 5

    val circleCenterX: Float
        get() = actionCircleRadiusExpanded + maxShadowRadius

    val circleCenterY: Float
        get() = actionCircleRadiusExpanded + maxShadowRadius - shadowOffsetY

    val circleCenterPoint: Point
        get() {
            mCenter.set(circleCenterX.toInt(), circleCenterY.toInt())
            return mCenter
        }


    init {
        init()
    }

    private fun init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.isAntiAlias = true
        mActionCircleRadius = resources.getDimensionPixelSize(R.dimen.qav_action_view_radius)
        actionCircleRadiusExpanded = resources.getDimensionPixelSize(R.dimen.qav_action_view_radius_expanded).toFloat()
        shadowOffsetY = resources.getDimensionPixelSize(R.dimen.qav_action_shadow_offset_y).toFloat()
        mIconPadding = resources.getDimensionPixelSize(R.dimen.qav_action_view_icon_padding)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((actionCircleRadiusExpanded * 2 + maxShadowRadius * 2).toInt(), (actionCircleRadiusExpanded * 2 + maxShadowRadius * 2).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        action.icon.state = actionState

        val x = circleCenterX
        val y = circleCenterY
        mBackgroundPaint!!.setShadowLayer(currentShadowRadius, 0f, shadowOffsetY, Color.parseColor("#50000000"))
        mBackgroundPaint!!.color = configHelper.backgroundColorStateList.getColorForState(actionState, Color.GRAY)

        canvas.drawCircle(x, y, interpolatedRadius, mBackgroundPaint!!)

        val icon = action.icon

        mTempPoint.x = x.toInt()
        mTempPoint.y = y.toInt()
        val bounds = getRectInsideCircle(mTempPoint, interpolatedRadius)
        bounds.inset(mIconPadding, mIconPadding)

        val aspect = icon.intrinsicWidth / icon.intrinsicHeight.toFloat()
        val desiredWidth = Math.min(bounds.width().toFloat(), bounds.height() * aspect).toInt()
        val desiredHeight = Math.min(bounds.height().toFloat(), bounds.width() / aspect).toInt()

        bounds.inset((bounds.width() - desiredWidth) / 2, (bounds.height() - desiredHeight) / 2)
        icon.bounds = bounds
        action.icon.draw(canvas)

    }


    private fun getRectInsideCircle(center: Point, radius: Float): Rect {
        val rect = Rect(0, 0, (radius * 2 / Math.sqrt(2.0)).toInt(), (radius * 2 / Math.sqrt(2.0)).toInt())
        rect.offsetTo(center.x - rect.width() / 2, center.y - rect.width() / 2)
        return rect
    }

    internal fun animateInterpolation(to: Float) {
        if (mCurrentAnimator != null && mCurrentAnimator!!.isRunning) {
            mCurrentAnimator!!.cancel()
        }
        mCurrentAnimator = ValueAnimator.ofFloat(interpolation, to)
        mCurrentAnimator!!.setDuration(150).addUpdateListener(this)
        mCurrentAnimator!!.start()
    }

    override fun isSelected(): Boolean {
        return mSelected
    }

    override fun setSelected(selected: Boolean) {
        mSelected = selected
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        interpolation = animation.animatedValue as Float
        invalidate()
    }
}
