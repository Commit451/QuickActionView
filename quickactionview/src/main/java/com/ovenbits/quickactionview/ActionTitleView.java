package com.ovenbits.quickactionview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

/**
 * Shows the title of the Action
 */
public class ActionTitleView extends TextView {

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
        setTextColor(mConfigHelper.getTextColorStateList());
        if (Build.VERSION.SDK_INT >= 16) {
            setBackgroundDrawable(mConfigHelper.getTextBackgroundDrawable());
        } else {
            setBackground(mConfigHelper.getTextBackgroundDrawable());
        }
    }
}
