package com.ovenbits.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

/**
 * Action that can be added to the {@link QuickActionView}
 */
public class Action {
    private int mId;
    private Drawable mIcon;
    private CharSequence mTitle;
    private Config mConfig;

    public Action(int id, @NonNull Drawable icon, @NonNull CharSequence title) {
        mId = id;
        mIcon = icon;
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public Drawable getIcon() {
        return mIcon;
    }

    @NonNull
    public CharSequence getTitle() {
        return mTitle;
    }

    @NonNull
    public Config getConfig() {
        return mConfig;
    }

    public void setConfig(@NonNull Config config) {
        mConfig = config;
    }

    /**
     * Configuration for the {@link Action} which controls the visuals.
     */
    public static class Config {
        protected ColorStateList mBackgroundColorStateList;
        protected int mTextColor;
        @DrawableRes
        protected int mTextBackgroundDrawable;

        protected Config(Context context) {
            int colorAccent = ColorUtils.getThemeAttrColor(context, R.attr.colorAccent);
            mBackgroundColorStateList = ColorStateList.valueOf(colorAccent);
            mTextBackgroundDrawable = R.drawable.qav_text_background;
            mTextColor = Color.WHITE;
        }

        public Config() {

        }


        public ColorStateList getBackgroundColorStateList() {
            return mBackgroundColorStateList;
        }

        public Config setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
            mBackgroundColorStateList = backgroundColorStateList;
            return this;
        }

        public Config setBackgroundColor(@ColorInt int backgroundColor) {
            mBackgroundColorStateList = ColorStateList.valueOf(backgroundColor);
            return this;
        }

        @Nullable
        public Drawable getTextBackgroundDrawable(Context context) {
            if (mTextBackgroundDrawable != 0) {
                return ContextCompat.getDrawable(context, mTextBackgroundDrawable);
            }
            return null;
        }

        public Config setTextBackgroundDrawable(@DrawableRes int textBackgroundDrawable) {
            mTextBackgroundDrawable = textBackgroundDrawable;
            return this;
        }

        public int getTextColor() {
            return mTextColor;
        }

        public Config setTextColor(int textColor) {
            mTextColor = textColor;
            return this;
        }
    }
}
