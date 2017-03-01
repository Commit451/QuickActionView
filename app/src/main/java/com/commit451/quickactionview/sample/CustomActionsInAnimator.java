package com.commit451.quickactionview.sample;

import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;

import com.commit451.quickactionview.Action;
import com.commit451.quickactionview.ActionView;
import com.commit451.quickactionview.ActionsInAnimator;
import com.commit451.quickactionview.QuickActionView;

/**
 * Example of how to create a custom animations in animator
 */
public class CustomActionsInAnimator implements ActionsInAnimator {

    private LinearInterpolator mInterpolator = new LinearInterpolator();
    private QuickActionView mQuickActionView;

    public CustomActionsInAnimator(QuickActionView view) {
        mQuickActionView = view;
    }

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
        Point center = mQuickActionView.getCenterPoint();
        if (Build.VERSION.SDK_INT >= 21 && center != null) {
            ViewAnimationUtils.createCircularReveal(scrim, center.x, center.y, 0, Math.max(scrim.getHeight(), scrim.getWidth()))
                    .start();
        } else {
            scrim.setAlpha(0f);
            scrim.animate().alpha(1f).setDuration(200);
        }
    }
}
