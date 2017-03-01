package com.commit451.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.ovenbits.quickactionview.R;

/**
 * Action that can be added to the {@link QuickActionView}
 */
public class Action {
    private int mId;
    private Drawable mIcon;
    private CharSequence mTitle;
    private Config mConfig;

    /**
     * Create a new Action, which you add to the QuickActionView with {@link QuickActionView#addAction(Action)}
     *
     * @param id    the action's unique id
     * @param icon  the drawable icon to display
     * @param title the title that appears above the Action button
     */
    public Action(int id, @NonNull Drawable icon, @NonNull CharSequence title) {
        if (id == 0) {
            throw new IllegalArgumentException("Actions must have a non-zero id");
        }
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
