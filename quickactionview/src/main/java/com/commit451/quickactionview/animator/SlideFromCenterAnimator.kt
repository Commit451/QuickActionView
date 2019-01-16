package com.commit451.quickactionview.animator

import android.graphics.Point
import android.view.View
import android.view.animation.OvershootInterpolator

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionView
import com.commit451.quickactionview.ActionsInAnimator
import com.commit451.quickactionview.ActionsOutAnimator

/**
 * Animator that slides actions out from the center point. This is the default animation
 * a QuickActionView uses
 */
class SlideFromCenterAnimator @JvmOverloads constructor(
        private val staggered: Boolean = false
) : ActionsInAnimator, ActionsOutAnimator {

    private val interpolator = OvershootInterpolator()

    override fun animateActionIn(action: Action, index: Int, view: ActionView, center: Point) {
        val actionCenter = view.circleCenterPoint
        actionCenter.offset(view.left, view.top)
        view.translationY = (center.y - actionCenter.y).toFloat()
        view.translationX = (center.x - actionCenter.x).toFloat()
        val viewPropertyAnimator = view.animate()
                .translationX(0f)
                .translationY(0f)
                .setInterpolator(interpolator)
                .setDuration(150)
        if (staggered) {
            viewPropertyAnimator.startDelay = (index * 100).toLong()
        }
    }

    override fun animateIndicatorIn(indicator: View) {
        indicator.alpha = 0f
        indicator.animate()
                .alpha(1f)
                .setDuration(200)
    }

    override fun animateScrimIn(scrim: View) {
        scrim.alpha = 0f
        scrim.animate()
                .alpha(1f)
                .setDuration(200)
    }

    override fun animateActionOut(action: Action, index: Int, view: ActionView, center: Point): Long {
        val actionCenter = view.circleCenterPoint
        actionCenter.offset(view.left, view.top)
        val translateViewPropertyAnimator = view.animate()
                .translationY((center.y - actionCenter.y).toFloat())
                .translationX((center.x - actionCenter.x).toFloat())
                .setInterpolator(interpolator)
                .setStartDelay(0)
                .setDuration(150)

        val alphaViewPropertyAnimator = view.animate()
                .alpha(0f)
                .setStartDelay(0)
                .setDuration(150)
        if (staggered) {
            translateViewPropertyAnimator.startDelay = (index * 100).toLong()
            alphaViewPropertyAnimator.startDelay = (index * 100).toLong()
        }

        return index * 100L + 150L
    }

    override fun animateIndicatorOut(indicator: View): Long {
        indicator.animate()
                .alpha(0f)
                .setDuration(200L)
        return 200L
    }

    override fun animateScrimOut(scrim: View): Long {
        scrim.animate()
                .alpha(0f)
                .setDuration(200)
        return 200L
    }
}
