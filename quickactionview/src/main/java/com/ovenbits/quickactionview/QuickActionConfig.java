package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.support.annotation.ColorInt;

/**
 * Configuration for the quick action
 * Created by John on 11/24/15.
 */
public class QuickActionConfig {

    protected ColorFilter mNormalColorFilter;
    protected ColorFilter mPressedColorFilter;
    protected @ColorInt int mNormalBackgroundColor;
    protected @ColorInt int mPressedBackgroundColor;
    protected @ColorInt int mTextBackgroundColor;
    protected @ColorInt int mTextColor;

    private QuickActionConfig() {
        //Use the builder!
    }

    public ColorFilter getNormalColorFilter() {
        return mNormalColorFilter;
    }

    public ColorFilter getPressedColorFilter() {
        return mPressedColorFilter;
    }

    public @ColorInt int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    public @ColorInt int getPressedBackgroundColor() {
        return mPressedBackgroundColor;
    }

    public @ColorInt int getTextBackgroundColor() {
        return mTextBackgroundColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public static class Builder {
        private QuickActionConfig config;

        public Builder(Context context) {
            config = getDefaultConfig(context);
        }

        public Builder setNormalColorFilter(ColorFilter colorFilter) {
            config.mNormalColorFilter = colorFilter;
            return this;
        }

        public Builder setPressedColorFilter(ColorFilter colorFilter) {
            config.mPressedColorFilter = colorFilter;
            return this;
        }

        public Builder setNormalBackgroundColor(@ColorInt int backgroundColor) {
            config.mNormalBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setPressedBackgroundColor(@ColorInt int backgroundColor) {
            config.mPressedBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTextBackgroundColor(@ColorInt int textBackgroundColor) {
            config.mTextBackgroundColor = textBackgroundColor;
            return this;
        }

        public Builder setTextColor(@ColorInt int textColor) {
            config.mTextColor = textColor;
            return this;
        }

        public QuickActionConfig build() {
            return config;
        }
    }

    public static QuickActionConfig getDefaultConfig(Context context) {
        QuickActionConfig config = new QuickActionConfig();
        int colorAccent = ColorUtils.getThemeAttrColor(context, R.attr.colorAccent);
        config.mNormalBackgroundColor = colorAccent;
        config.mPressedBackgroundColor = colorAccent;
        config.mNormalColorFilter = null;
        config.mPressedColorFilter = null;
        config.mTextBackgroundColor = colorAccent;
        config.mTextColor = Color.WHITE;
        return config;
    }
}
