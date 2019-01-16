package com.commit451.quickactionview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.appcompat.widget.AppCompatTextView

/**
 * Shows the title of the Action
 */
@SuppressLint("ViewConstructor")
internal class ActionTitleView(
        context: Context,
        private val action: Action,
        private val configHelper: ConfigHelper
) : AppCompatTextView(context) {

    init {
        init()
    }

    @SuppressLint("NewApi")
    private fun init() {
        setPadding(configHelper.textPaddingLeft, configHelper.textPaddingTop, configHelper.textPaddingRight, configHelper.textPaddingBottom)
        setTextColor(configHelper.textColor)
        textSize = configHelper.textSize.toFloat()
        if (Build.VERSION.SDK_INT >= 16) {
            setBackgroundDrawable(configHelper.getTextBackgroundDrawable(context))
        } else {
            background = configHelper.getTextBackgroundDrawable(context)
        }
        text = action.title
    }

}
