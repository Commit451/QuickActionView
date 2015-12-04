package com.ovenbits.quickactionview;

import android.animation.Animator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Sequencer for ViewPropertyAnimator
 */
public class AnimationSequencer implements Animator.AnimatorListener {

    private boolean mEnded = false;
    private boolean mStarted = false;
    private Queue<Runnable> mAfter = new LinkedList<>();
    private Queue<Runnable> mBefore = new LinkedList<>();

    @Override
    public void onAnimationStart(Animator animator) {
        mStarted = true;
        while (!mBefore.isEmpty()) {
            mBefore.poll().run();
        }
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mEnded = true;
        while (!mAfter.isEmpty()) {
            mAfter.poll().run();
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    public void after(Runnable runnable) {
        if (mEnded) {
            runnable.run();
        } else {
            mAfter.add(runnable);
        }
    }

    public void before(Runnable runnable) {
        if (mStarted) {
            runnable.run();
        } else {
            mBefore.add(runnable);
        }
    }
}