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
     * @return true if you are overriding animation, false if you want the default animation
     */
    boolean animateActionIn(Action action, int index, ActionView view, Point center);

    /**
     * Animate in the indicator as the QuickActionView shows
     * @param indicator the indicator view
     * @return true if you are overriding animation, false if you want the default animation
     */
    boolean animateIndicatorIn(View indicator);

    /**
     * Animate in the scrim as the QuickActionView shows
     * @param scrim the scrim view
     * @return true if you are overriding animation, false if you want the default animation
     */
    boolean animateScrimIn(View scrim);
}
