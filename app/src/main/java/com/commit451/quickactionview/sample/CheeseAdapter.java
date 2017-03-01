package com.commit451.quickactionview.sample;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.commit451.quickactionview.Action;
import com.commit451.quickactionview.QuickActionView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adapter for the recyclerview, which holds cheeses
 */
public class CheeseAdapter extends RecyclerView.Adapter<CheeseViewHolder> implements QuickActionView.OnActionSelectedListener {

    private Listener mListener;
    private ArrayList<Cheese> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            Cheese cheese = getItemAt(position);
            //Communicate to the activity that the item was long pressed
            mListener.onItemClicked(cheese);
        }
    };
    private QuickActionView mQuickActionView;

    public CheeseAdapter(Context context, Listener listener) {
        mQuickActionView = QuickActionView.make(context).addActions(R.menu.actions).setOnActionSelectedListener(this);
        mListener = listener;
        mValues = new ArrayList<>();
    }

    @Override
    public void onActionSelected(Action action, QuickActionView quickActionView) {
        View view = quickActionView.getLongPressedView();
        if (view != null) {
            int position = (int) view.getTag(R.id.list_position);
            Cheese cheese = getItemAt(position);
            Snackbar.make(view, "Clicked on " + cheese.getName() + " with action " + action.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void setData(Collection<Cheese> cheeses) {
        mValues.addAll(cheeses);
        notifyDataSetChanged();
    }

    @Override
    public CheeseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheeseViewHolder holder = CheeseViewHolder.newInstance(parent);
        holder.itemView.setOnClickListener(mOnClickListener);
        mQuickActionView.register(holder.itemView);
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

    public interface Listener {
        void onItemClicked(Cheese cheese);
    }
}
