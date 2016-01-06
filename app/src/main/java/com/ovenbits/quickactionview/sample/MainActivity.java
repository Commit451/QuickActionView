package com.ovenbits.quickactionview.sample;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.ActionView;
import com.ovenbits.quickactionview.ActionsInAnimator;
import com.ovenbits.quickactionview.ActionsOutAnimator;
import com.ovenbits.quickactionview.QuickActionView;

/**
 * Shows general use of the QuickActionView
 */
public class MainActivity extends AppCompatActivity {

    private ViewGroup mRoot;

    private final QuickActionView.OnActionSelectedListener mQuickActionListener = new QuickActionView.OnActionSelectedListener() {
        @Override
        public void onActionSelected(Action action, QuickActionView quickActionView) {
            Snackbar.make(mRoot, action.getTitle() + " was chosen", Snackbar.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoot = (ViewGroup) findViewById(R.id.root);
        findViewById(R.id.button_recyclerview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecyclerViewActivity.newIntent(MainActivity.this));
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final View view = findViewById(R.id.normal_parent);
        QuickActionView.make(this)
                .addActions(R.menu.actions)
                .setOnActionSelectedListener(mQuickActionListener)
                .register(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mRoot, "View was clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

        Action.Config actionConfig = new Action.Config()
                .setBackgroundColorStateList(ContextCompat.getColorStateList(this, R.drawable.sample_background_color))
                .setTextColor(Color.MAGENTA);

        QuickActionView.make(this)
                .addActions(R.menu.actions)
                .setOnActionSelectedListener(mQuickActionListener)
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.BLUE)
                .setTextSize(30)
                .setScrimColor(Color.parseColor("#99FFFFFF"))
                .setTextBackgroundDrawable(R.drawable.text_background)
                .setIndicatorDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.indicator))
                .setActionConfig(actionConfig, R.id.action_add_to_cart)
                .setActionsInAnimator(new CustomInAnimator())
                .setActionsOutAnimator(new CustomOutAnimator())
                .register(findViewById(R.id.custom_parent));
    }

    private class CustomInAnimator implements ActionsInAnimator {
        private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();

        @Override
        public void animateActionIn(Action action, int index, ActionView view, Point center) {
            Point actionCenter = view.getCircleCenterPoint();
            actionCenter.offset(view.getLeft(), view.getTop());
            view.setTranslationY(center.y - actionCenter.y);
            view.setTranslationX(center.x - actionCenter.x);
            view.animate().translationX(0).translationY(0).setInterpolator(mOvershootInterpolator).setStartDelay(index * 100).setDuration(150);
        }

        @Override
        public void animateIndicatorIn(View indicator) {
            indicator.setAlpha(0);
            indicator.animate().alpha(1).setDuration(200);
        }

        @Override
        public void animateScrimIn(View scrim) {
            scrim.setAlpha(0);
            scrim.animate().alpha(1).setDuration(200);
        }
    }

    private class CustomOutAnimator implements ActionsOutAnimator {
        private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();

        @Override
        public int animateActionOut(Action action, int index, ActionView view, Point center) {
            Point actionCenter = view.getCircleCenterPoint();
            actionCenter.offset(view.getLeft(), view.getTop());
            view.animate().translationY(center.y - actionCenter.y).translationX(center.x - actionCenter.x).setInterpolator(mOvershootInterpolator).setStartDelay(index * 100).setDuration(150);
            view.animate().alpha(0).setStartDelay(index * 100).setDuration(150);

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
}
