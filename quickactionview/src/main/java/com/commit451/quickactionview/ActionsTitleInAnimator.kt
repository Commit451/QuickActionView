package com.commit451.quickactionview

import android.view.View

/**
 * Custom animations for an [Action] label animating in
 */
interface ActionsTitleInAnimator {

    /**
     * Animate the action title view as the QuickActionView action title appears
     *
     * @param action The action being animated
     * @param index  The position of the action in its parent
     * @param view   The action title view
     */
    fun animateActionTitleIn(action: Action, index: Int, view: View)
}
