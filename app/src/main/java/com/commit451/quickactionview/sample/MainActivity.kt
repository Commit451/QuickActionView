package com.commit451.quickactionview.sample

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log

import com.commit451.quickactionview.Action
import com.commit451.quickactionview.QuickActionView
import com.commit451.quickactionview.animator.PopAnimator
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Shows general use of the QuickActionView
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "QuickActionView"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonRecyclerview.setOnClickListener { startActivity(RecyclerViewActivity.newIntent(this@MainActivity)) }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        QuickActionView.make(this)
                .addActions(R.menu.actions)
                .setOnActionSelectedListener { action, _ -> onAction(action) }
                .setOnShowListener { Log.d(TAG, "onShow") }
                .setOnDismissListener { Log.d(TAG, "onDismiss") }
                .setOnActionHoverChangedListener { _, _, hovering -> Log.d(TAG, "onHover $hovering") }
                .register(normalParent)
        normalParent.setOnClickListener {
            Snackbar.make(root, "View was clicked", Snackbar.LENGTH_SHORT)
                    .show()
        }

        @SuppressLint("ResourceType")
        val actionConfig = Action.Config(this)
                .setBackgroundColorStateList(ContextCompat.getColorStateList(this, R.drawable.sample_background_color)!!)
                .setTextColor(Color.MAGENTA)

        val popAnimator = PopAnimator(true)
        val actionTitleAnimator = CustomActionsTitleAnimator()
        val qav = QuickActionView.make(this)
                .addActions(R.menu.actions_2)
                .setOnActionSelectedListener { action, _ -> onAction(action) }
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.BLUE)
                .setTextSize(30)
                .setScrimColor(Color.parseColor("#99FFFFFF"))
                .setTextBackgroundDrawable(R.drawable.text_background)
                .setIndicatorDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.indicator)!!)
                .setActionConfig(actionConfig, R.id.action_add_to_cart)
                .setActionsOutAnimator(popAnimator)
                .setActionsTitleInAnimator(actionTitleAnimator)
                .setActionsTitleOutAnimator(actionTitleAnimator)
                .register(customParent)
        val customActionsInAnimator = CustomActionsInAnimator(qav)
        qav.setActionsInAnimator(customActionsInAnimator)
    }

    private fun onAction(action: Action) {
        Snackbar.make(root, action.title.toString() + " was chosen", Snackbar.LENGTH_SHORT)
                .show()
    }
}
