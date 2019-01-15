package com.commit451.quickactionview.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_recyclerview.*

import java.util.ArrayList

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
        val cheeses = ArrayList<Cheese>()
        for (i in 0..29) {
            cheeses.add(Cheeses.randomCheese())
        }
        adapter.setData(cheeses)
    }
}
