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
 * Animator that slides actions out from the center point. This is the default animation
 * a QuickActionView uses
 */
public class SlideFromCenterAnimator implements ActionsInAnimator, ActionsOutAnimator {

    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    private boolean mStaggered;

    public SlideFromCenterAnimator() {
        this(false);
    }

    public SlideFromCenterAnimator(boolean staggered) {
        mStaggered = staggered;
    }

    @Override
    public boolean animateActionIn(Action action, int index, ActionView view, Point center) {
        Point actionCenter = view.getCircleCenterPoint();
        actionCenter.offset(view.getLeft(), view.getTop());
        view.setTranslationY(center.y - actionCenter.y);
        view.setTranslationX(center.x - actionCenter.x);
        ViewPropertyAnimator viewPropertyAnimator = view.animate()
                .translationX(0)
                .translationY(0)
                .setInterpolator(mOvershootInterpolator)
                .setDuration(150);
        if (mStaggered) {
            viewPropertyAnimator.setStartDelay(index * 100);
        }
        return true;
    }

    @Override
    public boolean animateIndicatorIn(View indicator) {
        indicator.setAlpha(0);
        indicator.animate().alpha(1).setDuration(200);
        return true;
    }

    @Override
    public boolean animateScrimIn(View scrim) {
        scrim.setAlpha(0);
        scrim.animate().alpha(1).setDuration(200);
        return true;
    }

    @Override
    public int animateActionOut(Action action, int index, ActionView view, Point center) {
        Point actionCenter = view.getCircleCenterPoint();
        actionCenter.offset(view.getLeft(), view.getTop());
        ViewPropertyAnimator translateViewPropertyAnimator = view.animate()
                .translationY(center.y - actionCenter.y)
                .translationX(center.x - actionCenter.x)
                .setInterpolator(mOvershootInterpolator)
                .setStartDelay(0)
                .setDuration(150);

        ViewPropertyAnimator alphaViewPropertyAnimator = view.animate()
                .alpha(0)
                .setStartDelay(0)
                .setDuration(150);
        if (mStaggered) {
            translateViewPropertyAnimator.setStartDelay(index * 100);
            alphaViewPropertyAnimator.setStartDelay(index * 100);
        }

        return (index * 100) + 150;
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
