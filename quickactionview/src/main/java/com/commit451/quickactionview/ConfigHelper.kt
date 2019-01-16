package com.commit451.quickactionview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Determines which config to get values from, based on the state of each config.
 */
class ConfigHelper(
        private val actionConfig: Action.Config?,
        private val quickActionViewConfig: QuickActionView.Config
) {

    val textColor: Int
        @ColorInt
        get() = if (actionConfig != null && actionConfig.textColor != 0) {
            actionConfig.textColor
        } else quickActionViewConfig.textColor


    val backgroundColorStateList: ColorStateList
        get() = if (actionConfig?.backgroundColorStateList != null) {
            actionConfig.backgroundColorStateList
        } else quickActionViewConfig.backgroundColorStateList

    val typeface: Typeface?
        get() = quickActionViewConfig.typeface

    val textSize: Int
        get() = quickActionViewConfig.textSize

    val textPaddingTop: Int
        get() = quickActionViewConfig.textPaddingTop

    val textPaddingBottom: Int
        get() = quickActionViewConfig.textPaddingBottom

    val textPaddingLeft: Int
        get() = quickActionViewConfig.textPaddingLeft

    val textPaddingRight: Int
        get() = quickActionViewConfig.textPaddingRight

    fun getTextBackgroundDrawable(context: Context): Drawable? {
        return if (actionConfig?.getTextBackgroundDrawable(context) != null) {
            actionConfig.getTextBackgroundDrawable(context)
        } else quickActionViewConfig.getTextBackgroundDrawable(context)
    }

}
