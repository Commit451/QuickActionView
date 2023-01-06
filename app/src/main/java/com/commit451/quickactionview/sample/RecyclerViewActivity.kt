package com.commit451.quickactionview.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

/**
 * Shows usage of the QuickActionView from within a RecyclerView
 */
class RecyclerViewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RecyclerViewActivity::class.java)
        }
    }

    private lateinit var adapter: CheeseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        setSupportActionBar(toolbar)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        adapter = CheeseAdapter(this, object : CheeseAdapter.Listener {
            override fun onItemClicked(cheese: Cheese) {
                Snackbar.make(recyclerView, cheese.name + " was clicked", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })
        recyclerView.adapter = adapter

        loadCheeses()
    }

    private fun loadCheeses() {
        val cheeses = mutableListOf<Cheese>()
        for (i in 0..29) {
            cheeses.add(Cheeses.randomCheese())
        }
        adapter.setData(cheeses)
    }
}
