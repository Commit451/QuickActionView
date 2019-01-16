package com.commit451.quickactionview.animator

import android.graphics.Point
import android.view.View
import android.view.animation.OvershootInterpolator

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionView
import com.commit451.quickactionview.ActionsInAnimator
import com.commit451.quickactionview.ActionsOutAnimator

/**
 * Animator where actions pop in
 */
class PopAnimator @JvmOverloads constructor(
        private val staggered: Boolean = false
) : ActionsInAnimator, ActionsOutAnimator {

    private val interpolator = OvershootInterpolator()

    override fun animateActionIn(action: Action, index: Int, view: ActionView, center: Point) {
        view.scaleX = 0.01f
        view.scaleY = 0.01f
        val viewPropertyAnimator = view.animate().scaleY(1.0f)
                .scaleX(1.0f)
                .setDuration(200)
                .setInterpolator(interpolator)
        if (staggered) {
            viewPropertyAnimator.startDelay = (index * 100).toLong()
        }
    }

    override fun animateIndicatorIn(indicator: View) {
        indicator.alpha = 0f
        indicator.animate().alpha(1f).duration = 200
    }

    override fun animateScrimIn(scrim: View) {
        scrim.alpha = 0f
        scrim.animate().alpha(1f).duration = 200
    }

    override fun animateActionOut(action: Action, index: Int, view: ActionView, center: Point): Long {
        view.animate().scaleX(0.01f)
                .scaleY(0.01f)
                .alpha(0.0f)
                .setStartDelay(0).duration = 200
        return 200
    }

    override fun animateIndicatorOut(indicator: View): Long {
        indicator.animate().alpha(0f).duration = 200
        return 200
    }

    override fun animateScrimOut(scrim: View): Long {
        scrim.animate().alpha(0f).duration = 200
        return 200
    }
}
