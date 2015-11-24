package com.ovenbits.quickactionview.sample;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.QuickActionConfig;
import com.ovenbits.quickactionview.QuickActionView;

public class MainActivity extends AppCompatActivity {


    private ViewGroup mRoot;
    private float mTouchX;
    private float mTouchY;

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
        final QuickActionView quickActionView = new QuickActionView(this);
        quickActionView.setActions(R.menu.actions);
        //Give one of the quick actions custom colors
        QuickActionConfig quickActionConfig = new QuickActionConfig.Builder(this)
                .setBackgroundColor(Color.BLUE)
                .setTextBackgroundColor(Color.RED)
                .setTextColor(Color.BLACK)
                .build();
        quickActionView.setQuickActionConfig(R.id.actionAddToCart, quickActionConfig);
        quickActionView.setQuickActionListener(new QuickActionView.OnQuickActionSelectedListener() {
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

                if (quickActionView.getVisibility() == View.VISIBLE) {
                    return quickActionView.onTouchEvent(event);
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
                quickActionView.show(MainActivity.this, v, new Point((int) mTouchX, (int) mTouchY));
                return true;
            }
        });

    }
}
