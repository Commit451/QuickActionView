package com.ovenbits.quickactionview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.QuickActionView;
import com.ovenbits.quickactionview.animator.PopAnimator;

/**
 * Shows general use of the QuickActionView
 */
public class MainActivity extends AppCompatActivity {

    private final QuickActionView.OnShowListener mQuickActionShowListener = new QuickActionView.OnShowListener() {
        @Override
        public void onShow(QuickActionView quickActionView) {
            Log.d("MainActivity", "onShow");
        }
    };
    private final QuickActionView.OnDismissListener mQuickActionDismissListener = new QuickActionView.OnDismissListener() {
        @Override
        public void onDismiss(QuickActionView quickActionView) {
            Log.d("MainActivity", "onDismiss");
        }
    };
    private final QuickActionView.OnActionHoverChangedListener mOnActionHoverChangedListener = new QuickActionView.OnActionHoverChangedListener() {
        @Override
        public void onActionHoverChanged(Action action, QuickActionView quickActionView, boolean hovering) {
            Log.d("MainActivity", "onHover " + hovering);
        }
    };
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
                .setOnShowListener(mQuickActionShowListener)
                .setOnDismissListener(mQuickActionDismissListener)
                .setOnActionHoverChangedListener(mOnActionHoverChangedListener)
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

        PopAnimator popAnimator = new PopAnimator(true);
        QuickActionView.make(this)
                .addActions(R.menu.actions_2)
                .setOnActionSelectedListener(mQuickActionListener)
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.BLUE)
                .setTextSize(30)
                .setScrimColor(Color.parseColor("#99FFFFFF"))
                .setTextBackgroundDrawable(R.drawable.text_background)
                .setIndicatorDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.indicator))
                .setActionConfig(actionConfig, R.id.action_add_to_cart)
                .setActionsInAnimator(popAnimator)
                .setActionsOutAnimator(popAnimator)
                .register(findViewById(R.id.custom_parent));
    }
}
