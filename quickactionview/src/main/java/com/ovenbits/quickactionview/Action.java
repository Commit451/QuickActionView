package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

/**
 * Action that can be added to the {@link QuickActionView}
 */
public class Action {
    @IdRes
    private int mId;
    @DrawableRes
    private int mIcon;
    private String mTitle;
    private Config mConfig;

    public Action(@IdRes int id, @DrawableRes int icon, String title) {
        mId = id;
        mIcon = icon;
        mTitle = title;
    }

    public void setConfig(Config config) {
        mConfig = config;
    }

    /**
     * Configuration for the {@link Action} which controls the visuals.
     */
    public static class Config {

        protected ColorFilter mNormalColorFilter;
        protected ColorFilter mPressedColorFilter;
        protected @ColorInt int mNormalBackgroundColor;
        protected @ColorInt int mPressedBackgroundColor;
        protected @ColorInt int mTextBackgroundColor;
        protected @ColorInt int mTextColor;

        public Config(Context context) {
            int colorAccent = ColorUtils.getThemeAttrColor(context, R.attr.colorAccent);
            mNormalBackgroundColor = colorAccent;
            mPressedBackgroundColor = colorAccent;
            mNormalColorFilter = null;
            mPressedColorFilter = null;
            mTextBackgroundColor = Color.parseColor("#CC000000");
            mTextColor = Color.WHITE;
        }

        public ColorFilter getNormalColorFilter() {
            return mNormalColorFilter;
        }

        public Config setNormalColorFilter(ColorFilter colorFilter) {
            mNormalColorFilter = colorFilter;
            return this;
        }

        public ColorFilter getPressedColorFilter() {
            return mPressedColorFilter;
        }

        public Config setPressedColorFilter(ColorFilter colorFilter) {
            mPressedColorFilter = colorFilter;
            return this;
        }

        public int getNormalBackgroundColor() {
            return mNormalBackgroundColor;
        }

        public Config setNormalBackgroundColor(@ColorInt int backgroundColor) {
            mNormalBackgroundColor = backgroundColor;
            return this;
        }

        public int getPressedBackgroundColor() {
            return mPressedBackgroundColor;
        }

        public Config setPressedBackgroundColor(@ColorInt int backgroundColor) {
            mPressedBackgroundColor = backgroundColor;
            return this;
        }

        public int getTextBackgroundColor() {
            return mTextBackgroundColor;
        }

        public Config setTextBackgroundColor(@ColorInt int textBackgroundColor) {
            mTextBackgroundColor = textBackgroundColor;
            return this;
        }

        public Config setTextColor(@ColorInt int textColor) {
            mTextColor = textColor;
            return this;
        }
    }
}
