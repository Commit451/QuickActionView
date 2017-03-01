package com.commit451.quickactionview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Gets the colors
 */
class ColorUtils {

    private static TypedValue sTypedValue = new TypedValue();

    public static int getThemeAttrColor(Context context, int attributeColor) {
        context.getTheme().resolveAttribute(attributeColor, sTypedValue, true);
        return sTypedValue.data;
    }
}
