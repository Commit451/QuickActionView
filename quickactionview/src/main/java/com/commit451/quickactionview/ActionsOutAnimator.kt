package com.commit451.quickactionview

import android.graphics.Point
import android.view.View

/**
 * Custom animations for QuickActionView animating out
 */
interface ActionsOutAnimator {

    /**
     * Animate the action view as the QuickActionView dismisses
     *
     * @param action The action being animated
     * @param index  The position of the actionview in its parent
     * @param view   The action view
     * @param center The center of the indicator
     * @return The duration of this animation, in milliseconds
     */
    fun animateActionOut(action: Action, index: Int, view: ActionView, center: Point): Long

    /**
     * Animate the indicator view as the QuickActionView dismisses
     *
     * @param indicator The indicator view
     * @return The duration of this animation, in milliseconds
     */
    fun animateIndicatorOut(indicator: View): Long

    /**
     * Animate the scrim as the QuickActionView dismisses
     *
     * @param scrim The scrimView to animate
     * @return The duration of this animation, in milliseconds
     */
    fun animateScrimOut(scrim: View): Long

}
