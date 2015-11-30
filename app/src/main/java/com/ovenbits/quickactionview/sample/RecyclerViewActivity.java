package com.ovenbits.quickactionview.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.QuickActionView;

import java.util.ArrayList;

/**
 * Shows usage of the QuickActionView from within a RecyclerView
 * Created by John on 11/24/15.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, RecyclerViewActivity.class);
        return intent;
    }

    ViewGroup mRoot;
    RecyclerView mRecyclerView;
    CheeseAdapter mCheeseAdapter;
    QuickActionView mQuickActionView;

    float mTouchX;
    float mTouchY;
    Cheese mSelectedCheese;

    private final CheeseAdapter.Listener mCheeseAdapterListener = new CheeseAdapter.Listener() {
        @Override
        public void onItemClicked(Cheese cheese) {
            Snackbar.make(getWindow().getDecorView(), cheese.getName() + " was clicked", Snackbar.LENGTH_SHORT)
                    .show();

        }

        @Override
        public void onItemLongClicked(Cheese cheese, CheeseViewHolder holder) {
            mSelectedCheese = cheese;
            mQuickActionView.show(new Point((int) mTouchX, (int) mTouchY));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRoot = (ViewGroup) findViewById(R.id.root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mCheeseAdapter = new CheeseAdapter(mCheeseAdapterListener);
        mRecyclerView.setAdapter(mCheeseAdapter);

        mQuickActionView = new QuickActionView(this);
        mQuickActionView.setActions(R.menu.actions);
        mQuickActionView.setQuickActionListener(new QuickActionView.OnQuickActionSelectedListener() {

            @Override
            public void onQuickActionShow() {
            }

            @Override
            public void onQuickActionSelected(View view, int action) {
                Snackbar.make(mRoot, "Action selected for " + mSelectedCheese.getName(), Snackbar.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onDismiss() {

            }
        });
        findViewById(R.id.touch_interceptor).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = event.getX();
                mTouchY = event.getY();
                return false;
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mQuickActionView.getVisibility() == View.VISIBLE) {
                    return mQuickActionView.onTouchEvent(event);
                }
                return false;
            }
        });

        loadCheeses();
    }

    private void loadCheeses() {
        ArrayList<Cheese> cheeses = new ArrayList<>();
        for (int i=0; i<30; i++) {
            cheeses.add(Cheeses.getRandomCheese());
        }
        mCheeseAdapter.setData(cheeses);
    }
}
