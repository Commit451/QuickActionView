package com.commit451.quickactionview

import android.graphics.Point
import android.view.View

/**
 * Custom animations for QuickActionView animating in
 */
interface ActionsInAnimator {

    /**
     * Animate in the action view within the QuickActionView
     *
     * @param action the action
     * @param index  the index of the action in the list of actions
     * @param view   the action view to animate
     * @param center the final resting center point of the Action
     */
    fun animateActionIn(action: Action, index: Int, view: ActionView, center: Point)

    /**
     * Animate in the indicator as the QuickActionView shows
     *
     * @param indicator the indicator view
     */
    fun animateIndicatorIn(indicator: View)

    /**
     * Animate in the scrim as the QuickActionView shows
     *
     * @param scrim the scrim view
     */
    fun animateScrimIn(scrim: View)
}
