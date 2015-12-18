package com.ovenbits.quickactionview.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ovenbits.quickactionview.QuickActionView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adapter for the recyclerview, which holds cheeses
 * Created by John on 11/24/15.
 */
public class CheeseAdapter extends RecyclerView.Adapter<CheeseViewHolder> {

    public interface Listener {
        void onItemClicked(Cheese cheese);
    }
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
        QuickActionView.make(parent.getContext())
                .addActions(R.menu.actions)
                .register(holder.itemView);
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
