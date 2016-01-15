package com.ovenbits.quickactionview.animator;

import android.graphics.Point;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.ActionView;
import com.ovenbits.quickactionview.ActionsInAnimator;
import com.ovenbits.quickactionview.ActionsOutAnimator;

/**
 * Fades in the quick actions
 */
public class FadeAnimator implements ActionsInAnimator, ActionsOutAnimator {

    private LinearInterpolator mInterpolator = new LinearInterpolator();

    @Override
    public void animateActionIn(Action action, int index, ActionView view, Point center) {
        view.animate()
                .alpha(1.0f)
                .setDuration(200)
                .setInterpolator(mInterpolator);
    }

    @Override
    public void animateIndicatorIn(View indicator) {
        indicator.setAlpha(0);
        indicator.animate().alpha(1).setDuration(200);
    }

    @Override
    public void animateScrimIn(View scrim) {
        scrim.setAlpha(0f);
        scrim.animate().alpha(1f).setDuration(200);
    }

    @Override
    public int animateActionOut(Action action, int position, ActionView view, Point center) {
        view.animate().scaleX(0.1f)
                .scaleY(0.1f)
                .alpha(0.0f)
                .setStartDelay(0)
                .setDuration(200);
        return 200;
    }

    @Override
    public int animateIndicatorOut(View indicator) {
        indicator.animate().alpha(0).setDuration(200);
        return 200;
    }

    @Override
    public int animateScrimOut(View scrim) {
        scrim.animate().alpha(0).setDuration(200);
        return 200;
    }
}
