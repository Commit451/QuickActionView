package com.ovenbits.quickactionview;

import android.graphics.Point;
import android.view.View;

/**
 * Created by Alex on 12/18/15.
 */
public interface ActionsOutAnimator {

    /**
     * @param action The action being animated
     * @param index The position of the actionview in its parent
     * @param view The action view
     * @param center The center of the indicator
     * @return The duration of this animation
     */
    public int animateActionOut(Action action, int index, ActionView view, Point center);

    /**
     * @param indicator The indicator view
     * @return The duration of this animation
     */
    public int animateIndicatorOut(View indicator);

    /**
     * @param scrim The scrimView to animate
     * @return The duration of this animation
     */
    public int animateScrimOut(View scrim);

}
