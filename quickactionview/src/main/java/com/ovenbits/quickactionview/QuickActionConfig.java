package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Configuration for the quick action
 * Created by John on 11/24/15.
 */
public class QuickActionConfig {

    private @ColorInt int mBackgroundColor;
    private @ColorInt int mTextBackgroundColor;
    private @ColorInt int mTextColor;

    private QuickActionConfig() {
        //Use the builder!
    }

    public @ColorInt int getBackgroundColor() {
        return mBackgroundColor;
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

        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            config.mBackgroundColor = backgroundColor;
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
        config.mBackgroundColor = colorAccent;
        config.mTextBackgroundColor = colorAccent;
        config.mTextColor = Color.WHITE;
        return config;
    }
}
