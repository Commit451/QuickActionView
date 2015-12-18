package com.ovenbits.quickactionview;

import android.graphics.Point;
import android.view.View;

/**
 * Created by Alex on 12/18/15.
 */
public interface ActionsInAnimator {
    public void animateActionIn(Action action, int index, ActionView view, Point center);

    public void animateIndicatorIn(View indicator);

    public void animateScrimIn(View scrim);
}
