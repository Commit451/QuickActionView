package com.ovenbits.quickactionview;

import android.view.View;

/**
 * Custom animations for an {@link Action} label animating in
 */
public interface ActionsTitleOutAnimator {

    /**
     * Animate the action title view as the QuickActionView action title disappears
     *
     * @param action The action being animated
     * @param index  The position of the action in its parent
     * @param view   The action title view
     * @return The duration of this animation, in milliseconds, so that the view can be properly
     * hidden when the animation completes
     */
    int animateActionTitleOut(Action action, int index, View view);
}
