package com.ovenbits.quickactionview;

import android.view.View;

/**
 * Custom animations for an {@link Action} label animating in
 */
public interface ActionsTitleInAnimator {

    /**
     * Animate the action title view as the QuickActionView action title appears
     *
     * @param action The action being animated
     * @param index  The position of the action in its parent
     * @param view   The action title view
     */
    void animateActionTitleIn(Action action, int index, View view);
}
