package com.ovenbits.quickactionview.sample;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.QuickActionConfig;
import com.ovenbits.quickactionview.QuickActionView;

/**
 * Shows general use of the QuickActionView
 */
public class MainActivity extends AppCompatActivity {

    private ViewGroup mRoot;
    private float mTouchX;
    private float mTouchY;
    private SwitchCompat mCustomSwitch;
    private QuickActionView mNormalQuickActionView;
    private QuickActionView mCustomQuickActionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoot = (ViewGroup) findViewById(R.id.root);
        mCustomSwitch = (SwitchCompat) findViewById(R.id.custom);
        findViewById(R.id.button_recyclerview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecyclerViewActivity.newIntent(MainActivity.this));
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNormalQuickActionView = new QuickActionView(this);
        mNormalQuickActionView.setActions(R.menu.actions);
        mNormalQuickActionView.setQuickActionListener(new QuickActionView.OnQuickActionSelectedListener() {

            @Override
            public void onQuickActionShow() {
                Log.d("TEST", "onQuickActionShow");
            }

            @Override
            public void onQuickActionSelected(View view, int action) {
                Log.d("TEST", "onQuickActionSelected " + action);
                Snackbar.make(mRoot, "Action " + action + " was chosen", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDismiss() {
                Log.d("TEST", "onDismiss");
            }
        });
        final View view = findViewById(R.id.text);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = event.getX();
                mTouchY = event.getY();

                if (getActiveQuickActionView().getVisibility() == View.VISIBLE) {
                    return getActiveQuickActionView().onTouchEvent(event);
                }
                return false;
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //To disallow scrolling while the view is showing
                mRoot.requestDisallowInterceptTouchEvent(true);
                Log.d("TEST", "onLongClick");
                getActiveQuickActionView().show(v, new Point((int) mTouchX, (int) mTouchY));
                return true;
            }
        });
        createCustomQuickActionView();
    }

    private QuickActionView getActiveQuickActionView() {
        if (mCustomSwitch.isChecked()) {
            return mCustomQuickActionView;
        } else {
            return mNormalQuickActionView;
        }
    }

    private void createCustomQuickActionView() {
        //Give one of the quick actions custom colors
        QuickActionConfig quickActionConfig = new QuickActionConfig.Builder(this)
                .setNormalBackgroundColor(Color.BLUE)
                .setPressedBackgroundColor(Color.CYAN)
                .setNormalColorFilter(new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN))
                .setPressedColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN))
                .setTextBackgroundColor(Color.RED)
                .setTextColor(Color.BLACK)
                .build();
        mCustomQuickActionView = new QuickActionView(this)
                .setActions(R.menu.actions)
                .setCircleMode(QuickActionView.CIRCLE_MODE_FILL)
                .setScrimColor(Color.parseColor("#CC000000"))
                .setIconNormalColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN))
                .setIconPressedColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN))
                .setIconBackgroundNormalColor(Color.WHITE)
                .setIconBackgroundPressedColor(Color.RED)
                .setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf"))
                .setTextColor(Color.BLACK)
                .setTextBackgroundColor(Color.MAGENTA)
                .setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size))
                .setTouchCircleColor(Color.parseColor("#66FFFFFF"))
                .setQuickActionConfig(R.id.action_like, quickActionConfig);
    }
}
