package com.ovenbits.quickactionview;

import android.graphics.Point;
import android.view.View;

/**
 * Custom animations for QuickActionView animating in
 */
public interface ActionsInAnimator {

    /**
     * Animate in the action view within the QuickActionView
     * @param action the action
     * @param index the index of the action in the list of actions
     * @param view the action view to animate
     * @param center the final resting center point of the Action
     */
    void animateActionIn(Action action, int index, ActionView view, Point center);

    /**
     * Animate in the indicator as the QuickActionView shows
     * @param indicator the indicator view
     */
    void animateIndicatorIn(View indicator);

    /**
     * Animate in the scrim as the QuickActionView shows
     * @param scrim the scrim view
     */
    void animateScrimIn(View scrim);
}
