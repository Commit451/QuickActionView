package com.ovenbits.quickactionview.sample;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adapter for the recyclerview, which holds cheeses
 * Created by John on 11/24/15.
 */
public class CheeseAdapter extends RecyclerView.Adapter<CheeseViewHolder> {

    public interface Listener {
        void onItemClicked(Cheese cheese);
        void onItemLongClicked(Cheese cheese, CheeseViewHolder holder, Point touchPoint);
    }
    private Listener mListener;
    private ArrayList<Cheese> mValues;

    private float mLastTouchX;
    private float mLastTouchY;

    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mLastTouchX = event.getX();
            mLastTouchY = event.getY();
            return false;
        }
    };

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            Cheese cheese = getItemAt(position);
            CheeseViewHolder holder = (CheeseViewHolder) v.getTag(R.id.list_holder);
            //Create the point where the user pressed
            Point touchPoint = new Point((int) mLastTouchX, (int) mLastTouchY);
            //Communicate to the activity that the item was long pressed
            mListener.onItemLongClicked(cheese, holder, touchPoint);
            return true;
        }
    };

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            Cheese cheese = getItemAt(position);
            //Communicate to the activity that the item was long pressed
            mListener.onItemClicked(cheese);
        }
    };

    public CheeseAdapter(Listener listener) {
        mListener = listener;
        mValues = new ArrayList<>();
    }

    public void setData(Collection<Cheese> cheeses) {
        mValues.addAll(cheeses);
        notifyDataSetChanged();
    }

    @Override
    public CheeseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheeseViewHolder holder = CheeseViewHolder.newInstance(parent);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
        holder.itemView.setOnTouchListener(mOnTouchListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CheeseViewHolder holder, int position) {
        Cheese cheese = getItemAt(position);
        holder.bind(cheese);
        holder.itemView.setTag(R.id.list_position, position);
        holder.itemView.setTag(R.id.list_holder, holder);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private Cheese getItemAt(int position) {
        return mValues.get(position);
    }
}
