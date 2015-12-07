package com.ovenbits.quickactionview;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Determines which config to get values from, based on the state of each config.
 */
public class ConfigHelper {
    private Action.Config mActionConfig;
    private QAV.Config mQuickActionViewConfig;

    public ConfigHelper(@NonNull Action.Config actionConfig, @NonNull QAV.Config quickActionViewConfig) {
        mActionConfig = actionConfig;
        mQuickActionViewConfig = quickActionViewConfig;
    }

    public ColorStateList getIconColorStateList() {
        if (mActionConfig.getIconColorStateList() != null) {
            return mActionConfig.getIconColorStateList();
        }
        return mQuickActionViewConfig.getIconColorStateList();
    }


    public Drawable getTextBackgroundDrawable() {
        if (mActionConfig.getTextBackgroundDrawable() != null) {
            return mActionConfig.getTextBackgroundDrawable();
        }
        return mQuickActionViewConfig.getTextBackgroundDrawable();
    }


    public ColorStateList getTextColorStateList() {
        if (mActionConfig.getTextColorStateList() != null) {
            return mActionConfig.getTextColorStateList();
        }
        return mQuickActionViewConfig.getTextColorStateList();
    }


    public ColorStateList getBackgroundColorStateList() {
        if (mActionConfig.getBackgroundColorStateList() != null) {
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
