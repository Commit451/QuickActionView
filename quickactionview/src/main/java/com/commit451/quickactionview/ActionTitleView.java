package com.commit451.quickactionview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

/**
 * Shows the title of the Action
 */
@SuppressLint("ViewConstructor")
class ActionTitleView extends TextView {

    private Action mAction;
    private ConfigHelper mConfigHelper;

    public ActionTitleView(Context context, Action action, ConfigHelper configHelper) {
        super(context);
        mAction = action;
        mConfigHelper = configHelper;
        init();
    }

    @TargetApi(21)
    private void init() {
        setPadding(mConfigHelper.getTextPaddingLeft(), mConfigHelper.getTextPaddingTop(), mConfigHelper.getTextPaddingRight(), mConfigHelper.getTextPaddingBottom());
        setTextColor(mConfigHelper.getTextColor());
        setTextSize(mConfigHelper.getTextSize());
        if (Build.VERSION.SDK_INT >= 16) {
            setBackgroundDrawable(mConfigHelper.getTextBackgroundDrawable(getContext()));
        } else {
            setBackground(mConfigHelper.getTextBackgroundDrawable(getContext()));
        }
        setText(mAction.getTitle());
    }

}
