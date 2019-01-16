package com.commit451.quickactionview.animator

import android.view.View

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionsTitleInAnimator
import com.commit451.quickactionview.ActionsTitleOutAnimator

/**
 * Default animator which animates the action title in and out
 */
class FadeInFadeOutActionsTitleAnimator @JvmOverloads constructor(
        private val duration: Long = 100L
) : ActionsTitleInAnimator, ActionsTitleOutAnimator {

    override fun animateActionTitleIn(action: Action, index: Int, view: View) {
        view.alpha = 0.0f
        view.animate()
                .alpha(1.0f)
                .setDuration(duration)
    }

    override fun animateActionTitleOut(action: Action, index: Int, view: View): Long {
        view.animate()
                .alpha(0.0f)
                .setDuration(duration)
        return duration
    }
}
