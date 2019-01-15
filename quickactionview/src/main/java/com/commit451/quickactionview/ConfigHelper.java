package com.commit451.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * Determines which config to get values from, based on the state of each config.
 */
class ConfigHelper {
    private Action.Config mActionConfig;
    private QuickActionView.Config mQuickActionViewConfig;

    public ConfigHelper(@NonNull Action.Config actionConfig, @NonNull QuickActionView.Config quickActionViewConfig) {
        mActionConfig = actionConfig;
        mQuickActionViewConfig = quickActionViewConfig;
    }

    public Drawable getTextBackgroundDrawable(Context context) {
        if (mActionConfig != null && mActionConfig.getTextBackgroundDrawable(context) != null) {
            return mActionConfig.getTextBackgroundDrawable(context);
        }
        return mQuickActionViewConfig.getTextBackgroundDrawable(context);
    }


    @ColorInt
    public int getTextColor() {
        if (mActionConfig != null && mActionConfig.getTextColor() != 0) {
            return mActionConfig.getTextColor();
        }
        return mQuickActionViewConfig.getTextColor();
    }


    public ColorStateList getBackgroundColorStateList() {
        if (mActionConfig != null && mActionConfig.getBackgroundColorStateList() != null) {
            return mActionConfig.getBackgroundColorStateList();
        }
        return mQuickActionViewConfig.getBackgroundColorStateList();
    }

    public Typeface getTypeface() {
        return mQuickActionViewConfig.getTypeface();
    }

    public int getTextSize() {
        return mQuickActionViewConfig.getTextSize();
    }

    public int getTextPaddingTop() {
        return mQuickActionViewConfig.getTextPaddingTop();
    }

    public int getTextPaddingBottom() {
        return mQuickActionViewConfig.getTextPaddingBottom();
    }

    public int getTextPaddingLeft() {
        return mQuickActionViewConfig.getTextPaddingLeft();
    }

    public int getTextPaddingRight() {
        return mQuickActionViewConfig.getTextPaddingRight();
    }

}
