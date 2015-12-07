package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Action that can be added to the {@link QuickActionView}
 */
public class Action {
    private int mId;
    private Drawable mIcon;
    private CharSequence mTitle;
    private Config mConfig;

    public Action(int id, @DrawableRes Drawable icon, CharSequence title) {
        mId = id;
        mIcon = icon;
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public Config getConfig() {
        return mConfig;
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
        @ColorInt
        protected int mNormalBackgroundColor;
        @ColorInt
        protected int mPressedBackgroundColor;
        @ColorInt
        protected int mTextBackgroundColor;
        @ColorInt
        protected int mTextColor;

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
