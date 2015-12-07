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
            mTextBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.default_text_background);
            mTextColorStateList = ColorStateList.valueOf(Color.WHITE);
        }

        public Config() {

        }


        public Config(ColorStateList iconColorStateList, ColorStateList backgroundColorStateList, ColorStateList textColorStateList, Drawable textBackgroundDrawable) {
            mIconColorStateList = iconColorStateList;
            mBackgroundColorStateList = backgroundColorStateList;
            mTextColorStateList = textColorStateList;
            mTextBackgroundDrawable = textBackgroundDrawable;
        }

        public Config(@ColorInt int iconColor, @ColorInt int backgroundColor, @ColorInt int textColor, Drawable textBackgroundDrawable) {
            mIconColorStateList = ColorStateList.valueOf(iconColor);
            mBackgroundColorStateList = ColorStateList.valueOf(backgroundColor);
            mTextBackgroundDrawable = textBackgroundDrawable;
            mTextColorStateList = ColorStateList.valueOf(textColor);
        }


        public ColorStateList getIconColorStateList() {
            return mIconColorStateList;
        }

        public void setIconColorStateList(ColorStateList iconColorStateList) {
            mIconColorStateList = iconColorStateList;
        }

        public void setIconColor(@ColorInt int iconColor) {
            mIconColorStateList = ColorStateList.valueOf(iconColor);
        }

        public ColorStateList getBackgroundColorStateList() {
            return mBackgroundColorStateList;
        }

        public void setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
            mBackgroundColorStateList = backgroundColorStateList;
        }

        public void setBackgroundColor(@ColorInt int backgroundColor) {
            mBackgroundColorStateList = ColorStateList.valueOf(backgroundColor);
        }

        public Drawable getTextBackgroundDrawable() {
            return mTextBackgroundDrawable;
        }

        public void setTextBackgroundDrawable(Drawable textBackgroundDrawable) {
            mTextBackgroundDrawable = textBackgroundDrawable;
        }

        public ColorStateList getTextColorStateList() {
            return mTextColorStateList;
        }

        public void setTextColorStateList(ColorStateList textColorStateList) {
            mTextColorStateList = textColorStateList;
        }

        public void setTextColor(@ColorInt int textColor) {
            mTextColorStateList = ColorStateList.valueOf(textColor);
        }
    }
}
