package com.commit451.quickactionview.sample

import android.view.View
import android.view.animation.OvershootInterpolator

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionsTitleInAnimator
import com.commit451.quickactionview.ActionsTitleOutAnimator

/**
 * Default animator which animates the action title in and out
 */
class CustomActionsTitleAnimator : ActionsTitleInAnimator, ActionsTitleOutAnimator {

    companion object {
        private const val DURATION = 200L //ms
    }

    override fun animateActionTitleIn(action: Action, index: Int, view: View) {
        view.alpha = 0.0f
        view.scaleX = 0.0f
        view.scaleY = 0.0f
        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(OvershootInterpolator())
                .setDuration(DURATION)
    }

    override fun animateActionTitleOut(action: Action, index: Int, view: View): Long {
        view.animate()
                .alpha(0.0f)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(DURATION)
        return DURATION
    }
}
