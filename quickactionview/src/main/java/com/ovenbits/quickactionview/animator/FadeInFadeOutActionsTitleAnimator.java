package com.ovenbits.quickactionview.animator;

import android.view.View;

import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.ActionsTitleInAnimator;
import com.ovenbits.quickactionview.ActionsTitleOutAnimator;

/**
 * Default animator which animates the action title in and out
 */
public class FadeInFadeOutActionsTitleAnimator implements ActionsTitleInAnimator, ActionsTitleOutAnimator {

    private int mDuration; //ms

    public FadeInFadeOutActionsTitleAnimator() {
        this(100);
    }

    public FadeInFadeOutActionsTitleAnimator(int duration) {
        mDuration = duration;
    }

    @Override
    public void animateActionTitleIn(Action action, int index, View view) {
        view.setAlpha(0.0f);
        view.animate().alpha(1.0f).setDuration(mDuration);
    }

    @Override
    public int animateActionTitleOut(Action action, int index, View view) {
        view.animate().alpha(0.0f).setDuration(mDuration);
        return mDuration;
    }
}
