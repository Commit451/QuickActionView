package com.ovenbits.quickactionview;

import android.content.Context;
import android.widget.TextView;

/**
 * Shows the title of the Action
 */
public class TitleView extends TextView {

    private Action mAction;

    public TitleView(Context context, Action action) {
        super(context);
        mAction = action;
        init();
    }

    private void init() {
        setTextColor(mAction.getConfig().getTextColor());
        //TODO rounded and all that
        setBackgroundColor(mAction.getConfig().getTextBackgroundColor());
    }
}
