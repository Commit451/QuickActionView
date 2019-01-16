package com.commit451.quickactionview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Action that can be added to the [QuickActionView]
 */
class Action
/**
 * Create a new Action, which you add to the QuickActionView with [QuickActionView.addAction]
 *
 * @param id    the action's unique id
 * @param icon  the drawable icon to display
 * @param title the title that appears above the Action button
 */
(val id: Int, val icon: Drawable, val title: CharSequence) {

    var config: Config? = null

    init {
        if (id == 0) {
            throw IllegalArgumentException("Actions must have a non-zero id")
        }
    }

    /**
     * Configuration for the [Action] which controls the visuals.
     */
    class Config constructor(context: Context) {
        internal var backgroundColorStateList: ColorStateList = ColorStateList.valueOf(ColorUtils.getThemeAttrColor(context, R.attr.colorAccent))
        internal var textColor: Int = Color.WHITE
        @DrawableRes
        internal var textBackgroundDrawable: Int = R.drawable.qav_text_background

        fun getBackgroundColorStateList(): ColorStateList {
            return backgroundColorStateList
        }

        fun setBackgroundColorStateList(backgroundColorStateList: ColorStateList): Config {
            this.backgroundColorStateList = backgroundColorStateList
            return this
        }

        fun setBackgroundColor(@ColorInt backgroundColor: Int): Config {
            backgroundColorStateList = ColorStateList.valueOf(backgroundColor)
            return this
        }

        fun getTextBackgroundDrawable(context: Context): Drawable? {
            return if (textBackgroundDrawable != 0) {
                ContextCompat.getDrawable(context, textBackgroundDrawable)
            } else null
        }

        fun setTextBackgroundDrawable(@DrawableRes textBackgroundDrawable: Int): Config {
            this.textBackgroundDrawable = textBackgroundDrawable
            return this
        }

        fun getTextColor(): Int {
            return textColor
        }

        fun setTextColor(textColor: Int): Config {
            this.textColor = textColor
            return this
        }
    }
}
