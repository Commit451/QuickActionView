package com.ovenbits.quickactionview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * View that shows the action
 */
@SuppressLint("ViewConstructor")
public class ActionView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Paint mBackgroundPaint;

    private int mActionCircleRadius;
    private float mActionCircleRadiusExpanded;
    private float mActionShadowOffsetY;
    private int mIconPadding;
    private float mInterpolation;

    private Action mAction;
    private ConfigHelper mConfigHelper;
    private ValueAnimator mCurrentAnimator;
    private boolean mSelected = false;
    private Point mCenter = new Point();


    public ActionView(Context context, Action action, ConfigHelper configHelper) {
        super(context);
        mAction = action;
        mConfigHelper = configHelper;
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mActionCircleRadius = getResources().getDimensionPixelSize(R.dimen.qav_action_view_radius);
        mActionCircleRadiusExpanded = getResources().getDimensionPixelSize(R.dimen.qav_action_view_radius_expanded);
        mActionShadowOffsetY = getResources().getDimensionPixelSize(R.dimen.qav_action_shadow_offset_y);
        mIconPadding = getResources().getDimensionPixelSize(R.dimen.qav_action_view_icon_padding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (mActionCircleRadiusExpanded * 2 + getMaxShadowRadius() * 2), (int) (mActionCircleRadiusExpanded * 2 + getMaxShadowRadius() * 2));
    }

    float getActionCircleRadiusExpanded() {
        return mActionCircleRadiusExpanded;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSelected) {
            mAction.getIcon().setState(new int[]{android.R.attr.state_selected});
        } else {
            mAction.getIcon().setState(new int[]{});
        }

        float x = getCircleCenterX();
        float y = getCircleCenterY();
        mBackgroundPaint.setShadowLayer(getCurrentShadowRadius(), 0, getShadowOffsetY(), Color.parseColor("#50000000"));
        mBackgroundPaint.setColor(mConfigHelper.getBackgroundColorStateList().getColorForState(getDrawableState(), Color.GRAY));

        canvas.drawCircle(x, y, getInterpolatedRadius(), mBackgroundPaint);

        Drawable icon = mAction.getIcon();

        Rect bounds = getRectInsideCircle(new Point((int) x, (int) y), getInterpolatedRadius());
        bounds.inset(mIconPadding, mIconPadding);

        float aspect = icon.getIntrinsicWidth() / (float) (icon.getIntrinsicHeight());
        int desiredWidth = (int) Math.min(bounds.width(), bounds.height() * aspect);
        int desiredHeight = (int) Math.min(bounds.height(), bounds.width() / aspect);

        bounds.inset((bounds.width() - desiredWidth) / 2, (bounds.height() - desiredHeight) / 2);
        icon.setBounds(bounds);
        mAction.getIcon().draw(canvas);

    }


    private Rect getRectInsideCircle(Point center, float radius) {
        Rect rect = new Rect(0, 0, (int) ((radius * 2) / Math.sqrt(2)), (int) ((radius * 2) / Math.sqrt(2)));
        rect.offsetTo(center.x - rect.width() / 2, center.y - rect.width() / 2);
        return rect;
    }

    private float getMaxShadowRadius() {
        return mActionCircleRadiusExpanded / 5.0f;
    }

    public float getInterpolation() {
        return mInterpolation;
    }

    public void setInterpolation(float interpolation) {
        mInterpolation = interpolation;
    }

    private float getInterpolatedRadius() {
        return mActionCircleRadius + (mActionCircleRadiusExpanded - mActionCircleRadius) * mInterpolation;
    }

    private float getCurrentShadowRadius() {
        return getInterpolatedRadius() / 5;
    }

    protected float getCircleCenterX() {
        return mActionCircleRadiusExpanded + getMaxShadowRadius();
    }

    protected float getCircleCenterY() {
        return mActionCircleRadiusExpanded + getMaxShadowRadius() - getShadowOffsetY();
    }

    public Point getCircleCenterPoint() {
        mCenter.set((int) getCircleCenterX(), (int) getCircleCenterY());
        return mCenter;
    }

    private float getShadowOffsetY() {
        return mActionShadowOffsetY;
    }

    void animateInterpolation(float to) {
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()) {
            mCurrentAnimator.cancel();
        }
        mCurrentAnimator = ValueAnimator.ofFloat(mInterpolation, to);
        mCurrentAnimator.setDuration(150).addUpdateListener(this);
        mCurrentAnimator.start();
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mInterpolation = (float) animation.getAnimatedValue();
        invalidate();
    }

    public Action getAction() {
        return mAction;
    }
}
