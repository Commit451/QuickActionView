package com.commit451.quickactionview.sample

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.OnActionSelectedListener
import com.commit451.quickactionview.QuickActionView

/**
 * Adapter for the RecyclerView, which holds cheeses
 */
class CheeseAdapter(context: Context, private val listener: Listener) : RecyclerView.Adapter<CheeseViewHolder>(), OnActionSelectedListener {

    private val values = mutableListOf<Cheese>()

    private val quickActionView: QuickActionView = QuickActionView.make(context)
            .addActions(R.menu.actions)
            .setOnActionSelectedListener(this)

    override fun invoke(action: Action, quickActionView: QuickActionView) {
        val view = quickActionView.longPressedView
        if (view != null) {
            val position = view.getTag(R.id.list_position) as Int
            val cheese = values[position]
            Snackbar.make(view, "Clicked on ${cheese.name} with action ${action.title}", Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        val holder = CheeseViewHolder.newInstance(parent)
        holder.itemView.setOnClickListener { v ->
            val position = v.getTag(R.id.list_position) as Int
            val cheese = values[position]
            // Communicate to the activity that the item was clicked
            listener.onItemClicked(cheese)
        }
        quickActionView.register(holder.itemView)
        return holder
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese = values[position]
        holder.bind(cheese)
        holder.itemView.setTag(R.id.list_position, position)
        holder.itemView.setTag(R.id.list_holder, holder)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun setData(cheeses: Collection<Cheese>) {
        values.addAll(cheeses)
        notifyDataSetChanged()
    }

    interface Listener {
        fun onItemClicked(cheese: Cheese)
    }
}
