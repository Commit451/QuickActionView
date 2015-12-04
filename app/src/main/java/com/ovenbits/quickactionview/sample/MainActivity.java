package com.ovenbits.quickactionview.sample;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.QuickActionConfig;
import com.ovenbits.quickactionview.QuickActionView;

/**
 * Shows general use of the QuickActionView
 */
public class MainActivity extends AppCompatActivity {

    private ViewGroup mRoot;
    private QuickActionView mQuickActionView;

    private final QuickActionView.OnQuickActionViewListener mQuickActionListener = new QuickActionView.OnQuickActionViewListener() {
        @Override
        public void onQuickActionShow() {
            Log.d("TEST", "onQuickActionShow");
        }

        @Override
        public void onQuickActionSelected(View view, int action) {
            Log.d("TEST", "onQuickActionSelected " + action);
            Snackbar.make(mRoot, getResources().getResourceEntryName(action) + " was chosen", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onDismiss() {
            Log.d("TEST", "onDismiss");
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
        mQuickActionView = new QuickActionView(this);
        mQuickActionView.setActions(R.menu.actions);
        final View view = findViewById(R.id.normal_parent);
        mQuickActionView.setAnchor(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mRoot, "View was clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
        mQuickActionView.setOnQuickActionViewListener(mQuickActionListener);

        createCustomQuickActionView();
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
        new QuickActionView(this)
                .setActions(R.menu.actions)
                .setOnQuickActionViewListener(mQuickActionListener)
                .setAnchor(findViewById(R.id.custom_parent))
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
