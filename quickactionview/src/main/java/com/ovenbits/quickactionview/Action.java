package com.ovenbits.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
        protected ColorStateList mIconColorStateList;
        protected ColorStateList mBackgroundColorStateList;
        protected ColorStateList mTextColorStateList;
        protected Drawable mTextBackgroundDrawable;

        protected Config(Context context) {
            int colorAccent = ColorUtils.getThemeAttrColor(context, R.attr.colorAccent);
            mIconColorStateList = ColorStateList.valueOf(Color.WHITE);
            mBackgroundColorStateList = ColorStateList.valueOf(colorAccent);
            mTextBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.qav_text_background);
            mTextColorStateList = ColorStateList.valueOf(Color.WHITE);
        }

        public Config() {

        }

        public ColorStateList getIconColorStateList() {
            return mIconColorStateList;
        }

        public Config setIconColorStateList(ColorStateList iconColorStateList) {
            mIconColorStateList = iconColorStateList;
            return this;
        }

        public Config setIconColor(@ColorInt int iconColor) {
            mIconColorStateList = ColorStateList.valueOf(iconColor);
            return this;
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

        public Drawable getTextBackgroundDrawable() {
            return mTextBackgroundDrawable;
        }

        public Config setTextBackgroundDrawable(Drawable textBackgroundDrawable) {
            mTextBackgroundDrawable = textBackgroundDrawable;
            return this;
        }

        public ColorStateList getTextColorStateList() {
            return mTextColorStateList;
        }

        public Config setTextColorStateList(ColorStateList textColorStateList) {
            mTextColorStateList = textColorStateList;
            return this;
        }

        public Config setTextColor(@ColorInt int textColor) {
            mTextColorStateList = ColorStateList.valueOf(textColor);
            return this;
        }
    }
}
