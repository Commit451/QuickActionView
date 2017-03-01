package com.commit451.quickactionview.sample;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.commit451.quickactionview.Action;
import com.commit451.quickactionview.ActionsTitleInAnimator;
import com.commit451.quickactionview.ActionsTitleOutAnimator;

/**
 * Default animator which animates the action title in and out
 */
public class CustomActionsTitleAnimator implements ActionsTitleInAnimator, ActionsTitleOutAnimator {

    private static final int DURATION = 200; //ms

    @Override
    public void animateActionTitleIn(Action action, int index, View view) {
        view.setAlpha(0.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(DURATION);
    }

    @Override
    public int animateActionTitleOut(Action action, int index, View view) {
        view.animate()
                .alpha(0.0f)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(DURATION);
        return DURATION;
    }
}
