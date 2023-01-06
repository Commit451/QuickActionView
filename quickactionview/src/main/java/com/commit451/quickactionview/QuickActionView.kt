package com.commit451.quickactionview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import com.commit451.quickactionview.animator.FadeInFadeOutActionsTitleAnimator
import com.commit451.quickactionview.animator.SlideFromCenterAnimator
import java.util.*

/**
 * A QuickActionView, which shows actions when a view is long pressed.
 *
 * @see [https://github.com/Commit451/QuickActionView](https://github.com/Commit451/QuickActionView)
 */
@Suppress("unused")
class QuickActionView private constructor(private val context: Context) {

    companion object {

        /**
         * Create a QuickActionView which you can configure as desired, then
         * call [.register] to show it.
         *
         * @param context activity context
         * @return the QuickActionView for you to
         */
        fun make(context: Context): QuickActionView {
            return QuickActionView(context)
        }
    }

    private var shown = false
    private var onActionSelectedListener: OnActionSelectedListener? = null
    private var onDismissListener: OnDismissListener? = null
    private var onShowListener: OnShowListener? = null
    private var onActionHoverChangedListener: OnActionHoverChangedListener? = null
    private val actionDistance: Float
    private val actionPadding: Int
    private val actions = mutableListOf<Action>()
    private var extras: Bundle? = null
    private var quickActionViewLayout: QuickActionViewLayout? = null
    private val config: Config
    private var actionsInAnimator: ActionsInAnimator? = null
    private var actionsOutAnimator: ActionsOutAnimator? = null
    private var actionsTitleInAnimator: ActionsTitleInAnimator? = null
    private var actionsTitleOutAnimator: ActionsTitleOutAnimator? = null

    @ColorInt
    private var scrimColor = Color.parseColor("#99000000")
    private var indicatorDrawable: Drawable? = null
    private val registeredListeners = HashMap<View, RegisteredListener>()

    /**
     * Retrieve the view that has been long pressed
     *
     * @return the registered view that was long pressed to show the QuickActionView
     */
    var longPressedView: View? = null
        private set

    /**
     * Get the center point of the [QuickActionView] aka the point at which the actions will eminate from
     *
     * @return the center point, or null if the view has not yet been created
     */
    val centerPoint: Point?
        get() = if (quickActionViewLayout != null) {
            quickActionViewLayout!!.centerPoint
        } else null

    init {
        config = Config(context)
        indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.qav_indicator)
        actionDistance = context.resources.getDimensionPixelSize(R.dimen.qav_action_distance).toFloat()
        actionPadding = context.resources.getDimensionPixelSize(R.dimen.qav_action_padding)
        val defaultAnimator = SlideFromCenterAnimator(true)
        val defaultTitleAnimator = FadeInFadeOutActionsTitleAnimator()
        actionsInAnimator = defaultAnimator
        actionsOutAnimator = defaultAnimator
        actionsTitleInAnimator = defaultTitleAnimator
        actionsTitleOutAnimator = defaultTitleAnimator
    }

    private fun show(anchor: View, offset: Point) {
        if (shown) {
            throw RuntimeException("Show cannot be called when the QuickActionView is already visible")
        }
        shown = true

        val parent = anchor.parent
        if (parent is View) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        longPressedView = anchor

        val loc = IntArray(2)
        anchor.getLocationInWindow(loc)
        val point = Point(offset)
        point.offset(loc[0], loc[1])
        display(point)
    }


    /**
     * Register the QuickActionView to appear when the passed view is long pressed
     *
     * @param view the view to have long press responses
     * @return the QuickActionView
     */
    fun register(view: View): QuickActionView {
        val listener = RegisteredListener()
        registeredListeners[view] = listener
        view.setOnTouchListener(listener)
        view.setOnLongClickListener(listener)
        return this
    }

    /**
     * Unregister the view so that it can no longer be long pressed to show the QuickActionView
     *
     * @param view the view to unregister
     */
    fun unregister(view: View) {
        registeredListeners.remove(view)
        view.setOnTouchListener(null)
        view.setOnLongClickListener(null)
    }

    /**
     * Adds an action to the QuickActionView
     *
     * @param action the action to add
     * @return the QuickActionView
     */
    fun addAction(action: Action): QuickActionView {
        checkShown()
        actions.add(action)
        return this
    }

    /**
     * Adds a collection of actions to the QuickActionView
     *
     * @param actions the actions to add
     * @return the QuickActionView
     */
    fun addActions(actions: Collection<Action>): QuickActionView {
        checkShown()
        this.actions.addAll(actions)
        return this
    }

    /**
     * Add actions to the QuickActionView from the given menu resource id.
     *
     * @param menuId menu resource id
     * @return the QuickActionView
     */
    @SuppressLint("RestrictedApi")
    fun addActions(@MenuRes menuId: Int): QuickActionView {
        val menu = MenuBuilder(context)
        MenuInflater(context).inflate(menuId, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val action = Action(item.itemId, item.icon!!, item.title!!)
            addAction(action)
        }
        return this
    }

    /**
     * Removes all actions from the QuickActionView
     *
     * @return the QuickActionView
     */
    fun removeActions(): QuickActionView {
        actions.clear()
        return this
    }

    /**
     * Remove an individual action from the QuickActionView
     *
     * @param actionId the action id
     * @return the QuickActionView
     */
    fun removeAction(actionId: Int): QuickActionView {
        for (i in actions.indices) {
            if (actions[i].id == actionId) {
                actions.removeAt(i)
                return this
            }
        }
        throw IllegalArgumentException("No action exists for actionId$actionId")
    }

    /**
     * @param onActionSelectedListener the listener
     * @return the QuickActionView
     * @see OnActionSelectedListener
     */
    fun setOnActionSelectedListener(onActionSelectedListener: OnActionSelectedListener): QuickActionView {
        this.onActionSelectedListener = onActionSelectedListener
        return this
    }

    /**
     * @param onDismissListener the listener
     * @return the QuickActionView
     * @see OnDismissListener
     */
    fun setOnDismissListener(onDismissListener: OnDismissListener): QuickActionView {
        this.onDismissListener = onDismissListener
        return this
    }

    /**
     * @param onShowListener the listener
     * @return the QuickActionView
     * @see OnShowListener
     */
    fun setOnShowListener(onShowListener: OnShowListener): QuickActionView {
        this.onShowListener = onShowListener
        return this
    }

    /**
     * @param listener the listener
     * @return the QuickActionView
     * @see OnActionHoverChangedListener
     */
    fun setOnActionHoverChangedListener(listener: OnActionHoverChangedListener): QuickActionView {
        onActionHoverChangedListener = listener
        return this
    }

    /**
     * Set the indicator drawable (the drawable that appears at the point the user has long pressed
     *
     * @param indicatorDrawable the indicator drawable
     * @return the QuickActionView
     */
    fun setIndicatorDrawable(indicatorDrawable: Drawable): QuickActionView {
        this.indicatorDrawable = indicatorDrawable
        return this
    }

    /**
     * Set the scrim color (the background behind the QuickActionView)
     *
     * @param scrimColor the desired scrim color
     * @return the QuickActionView
     */
    fun setScrimColor(@ColorInt scrimColor: Int): QuickActionView {
        this.scrimColor = scrimColor
        return this
    }

    /**
     * Set the drawable that appears behind the Action text labels
     *
     * @param textBackgroundDrawable the desired drawable
     * @return the QuickActionView
     */
    fun setTextBackgroundDrawable(@DrawableRes textBackgroundDrawable: Int): QuickActionView {
        config.setTextBackgroundDrawable(textBackgroundDrawable)
        return this
    }


    /**
     * Set the background color state list for all action items
     *
     * @param backgroundColorStateList the desired colorstatelist
     * @return the QuickActionView
     */
    fun setBackgroundColorStateList(backgroundColorStateList: ColorStateList): QuickActionView {
        config.backgroundColorStateList = backgroundColorStateList
        return this
    }

    /**
     * Set the text color for the Action labels
     *
     * @param textColor the desired text color
     * @return the QuickActionView
     */
    fun setTextColor(@ColorInt textColor: Int): QuickActionView {
        config.textColor = textColor
        return this
    }

    /**
     * Set the action's background color. If you want to have a pressed state,
     * see [.setBackgroundColorStateList]
     *
     * @param backgroundColor the desired background color
     * @return the QuickActionView
     */
    fun setBackgroundColor(@ColorInt backgroundColor: Int): QuickActionView {
        config.setBackgroundColor(backgroundColor)
        return this
    }

    /**
     * Set the typeface for the Action labels
     *
     * @param typeface the desired typeface
     * @return the QuickActionView
     */
    fun setTypeface(typeface: Typeface): QuickActionView {
        config.typeface = typeface
        return this
    }

    /**
     * Set the text size for the Action labels
     *
     * @param textSize the desired textSize (in pixels)
     * @return the QuickActionView
     */
    fun setTextSize(textSize: Int): QuickActionView {
        config.textSize = textSize
        return this
    }

    /**
     * Set the text top padding for the Action labels
     *
     * @param textPaddingTop the top padding in pixels
     * @return the QuickActionView
     */
    fun setTextPaddingTop(textPaddingTop: Int): QuickActionView {
        config.textPaddingTop = textPaddingTop
        return this
    }

    /**
     * Set the text bottom padding for the Action labels
     *
     * @param textPaddingBottom the top padding in pixels
     * @return the QuickActionView
     */
    fun setTextPaddingBottom(textPaddingBottom: Int): QuickActionView {
        config.textPaddingBottom = textPaddingBottom
        return this
    }

    /**
     * Set the text left padding for the Action labels
     *
     * @param textPaddingLeft the top padding in pixels
     * @return the QuickActionView
     */
    fun setTextPaddingLeft(textPaddingLeft: Int): QuickActionView {
        config.textPaddingLeft = textPaddingLeft
        return this
    }

    /**
     * Set the text right padding for the Action labels
     *
     * @param textPaddingRight the top padding in pixels
     * @return the QuickActionView
     */
    fun setTextPaddingRight(textPaddingRight: Int): QuickActionView {
        config.textPaddingRight = textPaddingRight
        return this
    }

    /**
     * Override the animations for when the QuickActionView shows
     *
     * @param actionsInAnimator the animation overrides
     * @return this QuickActionView
     */
    fun setActionsInAnimator(actionsInAnimator: ActionsInAnimator): QuickActionView {
        this.actionsInAnimator = actionsInAnimator
        return this
    }

    /**
     * Override the animations for when the QuickActionView dismisses
     *
     * @param actionsOutAnimator the animation overrides
     * @return this QuickActionView
     */
    fun setActionsOutAnimator(actionsOutAnimator: ActionsOutAnimator): QuickActionView {
        this.actionsOutAnimator = actionsOutAnimator
        return this
    }

    /**
     * Override the animations for when the QuickActionView action title shows
     *
     * @param actionsTitleInAnimator the custom animator
     * @return this QuickActionView
     */
    fun setActionsTitleInAnimator(actionsTitleInAnimator: ActionsTitleInAnimator): QuickActionView {
        this.actionsTitleInAnimator = actionsTitleInAnimator
        return this
    }

    /**
     * Override the animations for when the QuickActionView dismisses
     *
     * @param actionsTitleOutAnimator the custom animator
     * @return this QuickActionView
     */
    fun setActionsTitleOutAnimator(actionsTitleOutAnimator: ActionsTitleOutAnimator): QuickActionView {
        this.actionsTitleOutAnimator = actionsTitleOutAnimator
        return this
    }

    /**
     * Set a custom configuration for the action with the given id
     *
     * @param config   the configuration to attach
     * @param actionId the action id
     * @return this QuickActionView
     */
    fun setActionConfig(config: Action.Config, @IdRes actionId: Int): QuickActionView {
        for (action in actions) {
            if (action.id == actionId) {
                action.config = config
                return this
            }
        }

        throw IllegalArgumentException("No Action exists with id $actionId")
    }

    private fun display(point: Point) {
        if (actions.isEmpty()) {
            throw IllegalStateException("You need to give the QuickActionView actions before calling show!")
        }

        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.TRANSLUCENT
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
        quickActionViewLayout = QuickActionViewLayout(context, actions, point)
        manager.addView(quickActionViewLayout, params)
        onShowListener?.invoke(this)
    }

    private fun animateHide() {
        val duration = quickActionViewLayout?.animateOut() ?: 0L
        quickActionViewLayout?.postDelayed({ removeView() }, duration)
    }

    private fun removeView() {
        val quickActionViewLayout = quickActionViewLayout
        if (quickActionViewLayout != null) {
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (checkAttachedToWindow(quickActionViewLayout)) {
                manager.removeView(quickActionViewLayout)
            }
            this.quickActionViewLayout = null
            shown = false
        }


        val parent = longPressedView?.parent
        if (parent is View) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun checkAttachedToWindow(view: View): Boolean {
        return if (Build.VERSION.SDK_INT >= 19) {
            view.isAttachedToWindow
        } else true
        //Unfortunately, we have no way of truly knowing on versions less than 19
    }

    private fun dismiss() {
        if (!shown) {
            throw RuntimeException("The QuickActionView must be visible to call dismiss()")
        }
        onDismissListener?.invoke(this@QuickActionView)
        animateHide()
    }

    private fun checkShown() {
        if (shown) {
            throw RuntimeException("QuickActionView cannot be configured if show has already been called.")
        }
    }

    /**
     * Get the extras associated with the QuickActionView. Allows for
     * saving state to the QuickActionView
     *
     * @return the bundle for the QuickActionView
     */
    fun getExtras(): Bundle? {
        return extras
    }

    /**
     * Set extras to associate with the QuickActionView to allow saving state
     *
     * @param extras the bundle
     * @return the QuickActionView
     */
    fun setExtras(extras: Bundle): QuickActionView {
        this.extras = extras
        return this
    }

    class Config private constructor(context: Context, var typeface: Typeface?, var textSize: Int, var textPaddingTop: Int) {
        private val defaultConfig: Action.Config
        var textPaddingBottom: Int = 0
        var textPaddingLeft: Int = 0
        var textPaddingRight: Int = 0

        var textColor: Int
            get() = defaultConfig.textColor
            set(@ColorInt textColor) {
                defaultConfig.textColor = textColor
            }


        var backgroundColorStateList: ColorStateList
            get() = defaultConfig.backgroundColorStateList
            set(backgroundColorStateList) {
                defaultConfig.backgroundColorStateList = backgroundColorStateList
            }

        constructor(context: Context) : this(context, null, context.resources.getInteger(R.integer.qav_action_title_view_text_size), context.resources.getDimensionPixelSize(R.dimen.qav_action_title_view_text_padding))

        init {
            textPaddingBottom = textPaddingTop
            textPaddingLeft = textPaddingTop
            textPaddingRight = textPaddingTop
            defaultConfig = Action.Config(context)
        }


        fun getTextBackgroundDrawable(context: Context): Drawable? {
            return defaultConfig.getTextBackgroundDrawable(context)
        }

        fun setTextBackgroundDrawable(@DrawableRes textBackgroundDrawable: Int) {
            defaultConfig.setTextBackgroundDrawable(textBackgroundDrawable)
        }

        fun setBackgroundColor(@ColorInt backgroundColor: Int) {
            defaultConfig.setBackgroundColor(backgroundColor)
        }
    }

    /**
     * Parent layout that actually houses all of the quick action views
     */
    private inner class QuickActionViewLayout(context: Context, actions: List<Action>, internal val centerPoint: Point) : FrameLayout(context) {
        private val indicatorView: View
        private val scrimView: View = View(context)
        private val actionViews = LinkedHashMap<Action, ActionView>()
        private val actionTitleViews = LinkedHashMap<Action, ActionTitleView>()
        private val lastTouch = PointF()
        private var animated = false

        private val maxActionAngle: Float
            get() {
                var max = 0f
                for ((index, actionView) in actionViews.values.withIndex()) {
                    max = getActionOffsetAngle(index, actionView)
                }
                return max
            }

        private val middleAngleOffset: Float
            get() = maxActionAngle / 2f

        init {
            scrimView.setBackgroundColor(scrimColor)
            val scrimParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(scrimView, scrimParams)
            indicatorView = View(context)
            if (Build.VERSION.SDK_INT >= 16) {
                indicatorView.background = indicatorDrawable
            } else {
                indicatorView.setBackgroundDrawable(indicatorDrawable)
            }
            val indicatorParams = FrameLayout.LayoutParams(indicatorDrawable!!.intrinsicWidth, indicatorDrawable!!.intrinsicHeight)
            addView(indicatorView, indicatorParams)
            for (action in actions) {
                val helper = ConfigHelper(action.config, config)
                val actionView = ActionView(context, action, helper)
                actionViews[action] = actionView
                val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                addView(actionView, params)
                if (!TextUtils.isEmpty(action.title)) {
                    val actionTitleView = ActionTitleView(context, action, helper)
                    actionTitleView.visibility = View.GONE
                    actionTitleViews[action] = actionTitleView
                    val titleParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    addView(actionTitleView, titleParams)
                }
            }
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            scrimView.layout(0, 0, measuredWidth, measuredHeight)

            indicatorView.layout(centerPoint.x - (indicatorView.measuredWidth / 2.0).toInt(),
                    centerPoint.y - (indicatorView.measuredHeight / 2.0).toInt(),
                    centerPoint.x + (indicatorView.measuredWidth / 2.0).toInt(),
                    centerPoint.y + (indicatorView.measuredHeight / 2.0).toInt())
            var index = 0
            for ((action, actionView) in actionViews) {

                val startAngle = getOptimalStartAngle(actionView.actionCircleRadiusExpanded)
                val point = getActionPoint(index, startAngle, actionView)
                point.offset(-actionView.circleCenterX, -actionView.circleCenterY)
                actionView.layout(point.x.toInt(), point.y.toInt(), (point.x + actionView.measuredWidth).toInt(), (point.y + actionView.measuredHeight).toInt())
                val titleView = actionTitleViews[action]
                if (titleView != null) {
                    val titleLeft = point.x + actionView.measuredWidth / 2 - titleView.measuredWidth / 2
                    val titleTop = point.y - 10f - titleView.measuredHeight.toFloat()
                    titleView.layout(titleLeft.toInt(), titleTop.toInt(), (titleLeft + titleView.measuredWidth).toInt(), (titleTop + titleView.measuredHeight).toInt())
                }
                index++
            }

            if (!animated) {
                animateActionsIn()
                animateIndicatorIn()
                animateScrimIn()
                animated = true
            }
        }

        private fun animateActionsIn() {
            for ((index, view) in actionViews.values.withIndex()) {
                actionsInAnimator?.animateActionIn(view.action, index, view, centerPoint)
            }
        }

        private fun animateIndicatorIn() {
            actionsInAnimator?.animateIndicatorIn(indicatorView)
        }

        private fun animateScrimIn() {
            actionsInAnimator?.animateScrimIn(scrimView)
        }

        internal fun animateOut(): Long {
            var maxDuration = 0L
            maxDuration = Math.max(maxDuration, animateActionsOut())
            maxDuration = Math.max(maxDuration, animateScrimOut())
            maxDuration = Math.max(maxDuration, animateIndicatorOut())
            maxDuration = Math.max(maxDuration, animateLabelsOut().toLong())
            return maxDuration
        }

        private fun animateActionsOut(): Long {
            var maxDuration = 0L
            for ((index, view) in actionViews.values.withIndex()) {
                view.clearAnimation()
                maxDuration = Math.max(actionsOutAnimator?.animateActionOut(view.action, index, view, centerPoint) ?: 0L, maxDuration)
            }
            return maxDuration
        }

        private fun animateLabelsOut(): Int {
            for (view in actionTitleViews.values) {
                view.animate().alpha(0f).duration = 100
            }
            return 200
        }

        private fun animateIndicatorOut(): Long {
            indicatorView.clearAnimation()
            return actionsOutAnimator!!.animateIndicatorOut(indicatorView)
        }

        private fun animateScrimOut(): Long {
            scrimView.clearAnimation()
            return actionsOutAnimator!!.animateScrimOut(scrimView)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (shown) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        lastTouch.set(event.rawX, event.rawY)
                        for ((index, actionView) in actionViews.values.withIndex()) {
                            if (insideCircle(getActionPoint(index, getOptimalStartAngle(actionView.actionCircleRadiusExpanded), actionView), actionView.actionCircleRadiusExpanded, event.rawX, event.rawY)) {
                                if (!actionView.isSelected) {
                                    actionView.isSelected = true
                                    actionView.animateInterpolation(1.0f)
                                    val actionTitleView = actionTitleViews[actionView.action]
                                    if (actionTitleView != null) {
                                        actionTitleView.visibility = View.VISIBLE
                                        actionTitleView.bringToFront()
                                        actionsTitleInAnimator!!.animateActionTitleIn(actionView.action, index, actionTitleView)
                                    }
                                    onActionHoverChangedListener?.invoke(actionView.action, this@QuickActionView, true)
                                }
                            } else {
                                if (actionView.isSelected) {
                                    actionView.isSelected = false
                                    actionView.animateInterpolation(0.0f)
                                    val actionTitleView = actionTitleViews[actionView.action]
                                    if (actionTitleView != null) {
                                        val timeTaken = actionsTitleOutAnimator!!.animateActionTitleOut(actionView.action, index, actionTitleView)
                                        actionTitleView.postDelayed({ actionTitleView.visibility = View.GONE }, timeTaken)
                                    }
                                    onActionHoverChangedListener?.invoke(actionView.action, this@QuickActionView, false)
                                }
                            }
                        }
                        invalidate()
                    }
                    MotionEvent.ACTION_UP -> {
                        for ((key, value) in actionViews) {
                            if (value.isSelected) {
                                onActionSelectedListener?.invoke(key, this@QuickActionView)
                                break
                            }
                        }

                        dismiss()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> dismiss()
                }
            }
            return true
        }


        private fun getActionPoint(index: Int, startAngle: Float, view: ActionView): PointF {
            val point = PointF(centerPoint)
            val angle = (Math.toRadians(startAngle.toDouble()) + getActionOffsetAngle(index, view)).toFloat()
            point.offset((Math.cos(angle.toDouble()) * getTotalRadius(view.actionCircleRadiusExpanded)).toInt().toFloat(), (Math.sin(angle.toDouble()) * getTotalRadius(view.actionCircleRadiusExpanded)).toInt().toFloat())
            return point
        }

        private fun getActionOffsetAngle(index: Int, view: ActionView): Float {
            return (index * (2 * Math.atan2((view.actionCircleRadiusExpanded + actionPadding).toDouble(), getTotalRadius(view.actionCircleRadiusExpanded).toDouble()))).toFloat()
        }

        private fun getTotalRadius(actionViewRadiusExpanded: Float): Float {
            return actionDistance + Math.max(indicatorView.width, indicatorView.height).toFloat() + actionViewRadiusExpanded
        }

        private fun getOptimalStartAngle(actionViewRadiusExpanded: Float): Float {
            if (measuredWidth > 0) {
                val radius = getTotalRadius(actionViewRadiusExpanded)

                val top = -centerPoint.y
                val topIntersect = !java.lang.Double.isNaN(Math.acos((top / radius).toDouble()))

                val horizontalOffset = (centerPoint.x - measuredWidth / 2.0f) / (measuredWidth / 2.0f)

                val angle: Float
                val offset = Math.pow(Math.abs(horizontalOffset).toDouble(), 1.2).toFloat() * Math.signum(horizontalOffset)
                angle = if (topIntersect) {
                    90 + 90 * offset
                } else {
                    270 - 90 * offset
                }
                normalizeAngle(angle.toDouble())

                return (angle - Math.toDegrees(middleAngleOffset.toDouble())).toFloat()
            }
            return (270 - Math.toDegrees(middleAngleOffset.toDouble())).toFloat()
        }


        fun normalizeAngle(angleDegrees: Double): Float {
            var finalAngleDegrees = angleDegrees
            finalAngleDegrees = finalAngleDegrees % 360
            finalAngleDegrees = (finalAngleDegrees + 360) % 360
            return finalAngleDegrees.toFloat()
        }

        private fun insideCircle(center: PointF, radius: Float, x: Float, y: Float): Boolean {
            return distance(center, x, y) < radius
        }

        private fun distance(point: PointF, x: Float, y: Float): Float {
            return Math.sqrt(Math.pow((x - point.x).toDouble(), 2.0) + Math.pow((y - point.y).toDouble(), 2.0)).toFloat()
        }
    }

    /**
     * A class to combine a long click listener and a touch listener, to register views with
     */
    private inner class RegisteredListener : View.OnLongClickListener, View.OnTouchListener {

        private var touchX: Float = 0.toFloat()
        private var touchY: Float = 0.toFloat()

        override fun onLongClick(v: View): Boolean {
            show(v, Point(touchX.toInt(), touchY.toInt()))
            return false
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            touchX = event.x
            touchY = event.y
            if (shown) {
                quickActionViewLayout?.onTouchEvent(event)
            }
            return shown
        }
    }
}

/**
 * Listener for when an action is selected (hovered, then released)
 */
typealias OnActionSelectedListener = (action: Action, quickActionView: QuickActionView) -> Unit

/**
 * Listener for when an action has its hover state changed (hovering or stopped hovering)
 */
typealias OnActionHoverChangedListener = (action: Action, quickActionView: QuickActionView, hovering: Boolean) -> Unit

/**
 * Listen for when the QuickActionView is dismissed
 */
typealias OnDismissListener = (quickActionView: QuickActionView) -> Unit

/**
 * Listener for when the QuickActionView is shown
 */
typealias OnShowListener = (quickActionView: QuickActionView) -> Unit
