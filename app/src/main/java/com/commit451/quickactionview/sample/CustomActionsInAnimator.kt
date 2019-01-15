package com.commit451.quickactionview.sample

import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionView
import com.commit451.quickactionview.ActionsInAnimator
import com.commit451.quickactionview.QuickActionView

/**
 * Example of how to create a custom animations in animator
 */
class CustomActionsInAnimator(private val quickActionView: QuickActionView) : ActionsInAnimator {

    private val interpolator = LinearInterpolator()

    override fun animateActionIn(action: Action, index: Int, view: ActionView, center: Point) {
        view.animate()
                .alpha(1.0f)
                .setInterpolator(interpolator)
                .setDuration(200)
    }

    override fun animateIndicatorIn(indicator: View) {
        indicator.alpha = 0f
        indicator.animate()
                .alpha(1f)
                .setDuration(200)
    }

    override fun animateScrimIn(scrim: View) {
        val center = quickActionView.centerPoint
        if (Build.VERSION.SDK_INT >= 21 && center != null) {
            ViewAnimationUtils.createCircularReveal(scrim, center.x, center.y, 0f, Math.max(scrim.height, scrim.width).toFloat())
                    .start()
        } else {
            scrim.alpha = 0f
            scrim.animate()
                    .alpha(1f)
                    .setDuration(200)
        }
    }
}
