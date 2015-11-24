package com.ovenbits.quickactionview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Gets the colors
 * Created by John on 11/24/15.
 */
public class ColorUtils {

    private static TypedValue sTypedValue = new TypedValue();

    public static int getThemeAttrColor(Context context, int attributeColor) {
        context.getTheme().resolveAttribute(attributeColor, sTypedValue, true);
        return sTypedValue.data;
    }
}
