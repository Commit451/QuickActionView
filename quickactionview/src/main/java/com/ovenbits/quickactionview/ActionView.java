package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * View that shows the action
 */
public class ActionView extends View {

    private Paint mBackgroundPaint;
    private Paint mIconPaint;

    private int mActionRadius;

    private Action mAction;

    public ActionView(Context context, Action action) {
        super(context);
        mAction = action;
        init();
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mIconPaint = new Paint();
        mActionRadius = getResources().getDimensionPixelSize(R.dimen.qav_action_view_radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = getWidth()/2;
        float y = getWidth()/2;
        canvas.drawCircle(x, y, mActionRadius, mBackgroundPaint);
        //TODO 5.0+ = elevation?
        mBackgroundPaint.setShadowLayer(mActionRadius, x, y, Color.parseColor("#50000000"));

//        if (isPressed) {
//            icon.setColorFilter(config.getPressedColorFilter());
//        } else {
//            icon.setColorFilter(config.getNormalColorFilter());
//        }
//        Rect bounds = getRectInsideCircle(point, radius);
//        bounds.inset((int) mTextBackgroundPadding, (int) mTextBackgroundPadding);
//
//        float aspect = icon.getIntrinsicWidth() / (float) (icon.getIntrinsicHeight());
//        int desiredWidth = (int) Math.min(bounds.width(), bounds.height() * aspect);
//        int desiredHeight = (int) Math.min(bounds.height(), bounds.width() / aspect);
//
//        bounds.inset((bounds.width() - desiredWidth) / 2, (bounds.height() - desiredHeight) / 2);
//        icon.setBounds(bounds);
//        icon.draw(canvas);

    }
}
