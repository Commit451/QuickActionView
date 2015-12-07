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

    public ActionTitleView(Context context, Action action) {
        super(context);
        mAction = action;
        init();
    }

    @TargetApi(21)
    private void init() {
        setTextColor(mAction.getConfig().getTextColorStateList());
        if (Build.VERSION.SDK_INT >= 16) {
            setBackgroundDrawable(mAction.getConfig().getTextBackgroundDrawable());
        } else {
            setBackground(mAction.getConfig().getTextBackgroundDrawable());
        }
    }
}
