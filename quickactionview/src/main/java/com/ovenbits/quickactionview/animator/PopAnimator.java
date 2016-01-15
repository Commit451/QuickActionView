package com.ovenbits.quickactionview.animator;

import android.graphics.Point;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;

import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.ActionView;
import com.ovenbits.quickactionview.ActionsInAnimator;
import com.ovenbits.quickactionview.ActionsOutAnimator;

/**
 * Animator where actions pop in
 */
public class PopAnimator implements ActionsInAnimator, ActionsOutAnimator {

    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    private boolean mStaggered;

    public PopAnimator() {
        this(false);
    }

    public PopAnimator(boolean staggered) {
        mStaggered = staggered;
    }

    @Override
    public void animateActionIn(Action action, int index, ActionView view, Point center) {
        view.setScaleX(0.01f);
        view.setScaleY(0.01f);
        ViewPropertyAnimator viewPropertyAnimator = view.animate().scaleY(1.0f)
                .scaleX(1.0f)
                .setDuration(200)
                .setInterpolator(mOvershootInterpolator);
        if (mStaggered) {
            viewPropertyAnimator.setStartDelay(index * 100);
        }
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
        view.animate().scaleX(0.01f)
                .scaleY(0.01f)
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
