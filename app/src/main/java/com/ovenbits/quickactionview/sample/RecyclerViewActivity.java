package com.ovenbits.quickactionview.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Shows usage of the QuickActionView from within a RecyclerView
 * Created by John on 11/24/15.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private final CheeseAdapter.Listener mCheeseAdapterListener = new CheeseAdapter.Listener() {
        @Override
        public void onItemClicked(Cheese cheese) {
            Snackbar.make(getWindow().getDecorView(), cheese.getName() + " was clicked", Snackbar.LENGTH_SHORT)
                    .show();
        }
    };
    ViewGroup mRoot;
    RecyclerView mRecyclerView;
    CheeseAdapter mCheeseAdapter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, RecyclerViewActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRoot = (ViewGroup) findViewById(R.id.root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mCheeseAdapter = new CheeseAdapter(this, mCheeseAdapterListener);
        mRecyclerView.setAdapter(mCheeseAdapter);

        loadCheeses();
    }

    private void loadCheeses() {
        ArrayList<Cheese> cheeses = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            cheeses.add(Cheeses.getRandomCheese());
        }
        mCheeseAdapter.setData(cheeses);
    }
}
