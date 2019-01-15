package com.commit451.quickactionview

import android.content.Context
import android.util.TypedValue

/**
 * Gets the colors
 */
internal object ColorUtils {

    private val typedValue = TypedValue()

    fun getThemeAttrColor(context: Context, attributeColor: Int): Int {
        context.theme.resolveAttribute(attributeColor, typedValue, true)
        return typedValue.data
    }
}
