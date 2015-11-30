package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * QuickActionView, which shows actions emanating from the location where a user long presses.
 * Created by Alex on 6/12/15.
 */
public class QuickActionView extends View {

    private static final int DEFAULT_DISTANCE = 70;
    private static final int DEFAULT_ACTION_RADIUS = 25;
    private static final int DEFAULT_ACTION_RADIUS_EXPANDED = 30;
    private static final int DEFAULT_TEXT_PADDING = 8;
    private static final int DEFAULT_TEXT_BACKGROUND_PADDING = 8;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CIRCLE_MODE_FILL, CIRCLE_MODE_STROKE})
    public @interface CircleMode {}
    public static final int CIRCLE_MODE_FILL = 0;
    public static final int CIRCLE_MODE_STROKE = 1;

    public interface OnQuickActionSelectedListener {

        void onQuickActionShow();

        void onQuickActionSelected(View view, int action);

        void onDismiss();
    }

    private float mLineDistance;
    private float mTouchCircleRadius;
    private float mActionCircleRadius;
    private float mActionCircleRadiusExpanded;
    private float mStartAngle = 270;
    private float mAngularSpacing = 15;

    private float mLastTouchX = 0;
    private float mLastTouchY = 0;
    private int mScrimColor = Color.parseColor("#33000000");

    private float mTextPadding = 0;
    private float mTextBackgroundPadding;

    private PopupMenu mActions;
    private HashMap<Integer, QuickActionConfig> mQuickActionConfigHashMap;
    //The config that will be used if a custom config does not exist
    private QuickActionConfig mDefaultQuickActionConfig;
    private OnQuickActionSelectedListener mListener;

    private Paint mTouchCirclePaint;
    private Paint mActionPaint;
    private Paint mTextPaint;
    private Paint mTextBorderPaint;

    private Point mInnerCirclePoint = new Point();
    private RectF mTextRect = new RectF();

    /**
     * Creates a QuickActionView. Perform setup, such as {@link #setActions(int)} and then call {@link #show(Point)}
     * @param context the context
     */
    public QuickActionView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mQuickActionConfigHashMap = new HashMap<>();
        mDefaultQuickActionConfig = QuickActionConfig.getDefaultConfig(getContext());
        setVisibility(View.GONE);
        setClickable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mActionCircleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS, getResources().getDisplayMetrics());
        mActionCircleRadiusExpanded = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS_EXPANDED, getResources().getDisplayMetrics());
        mLineDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DISTANCE, getResources().getDisplayMetrics());
        mTouchCircleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS, getResources().getDisplayMetrics());
        mTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_PADDING, getResources().getDisplayMetrics());
        mTextBackgroundPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_BACKGROUND_PADDING, getResources().getDisplayMetrics());

        mTouchCirclePaint = new Paint();
        mTouchCirclePaint.setAntiAlias(true);
        mTouchCirclePaint.setStyle(Paint.Style.STROKE);
        mTouchCirclePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mTouchCirclePaint.setColor(Color.parseColor("#33FFFFFF"));
        mActionPaint = new Paint();
        mActionPaint.setAntiAlias(true);
        mActionPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);

        mTextBorderPaint = new Paint();
        mTextBorderPaint.setAntiAlias(true);
        mTextBorderPaint.setStyle(Paint.Style.FILL);

    }

    /**
     * Show the QuickActionView, with the origin at the View, and a point to specify the offset
     * @param origin the view to use as the origin
     * @param offset the offset from the origin
     */
    public void show(View origin, Point offset) {
        int[] loc = new int[2];
        origin.getLocationInWindow(loc);

        Point point = new Point(offset);
        point.offset(loc[0], loc[1]);
        show(point);
    }

    /**
     * Show the QuickActionView, centered at the point specified
     * @param point the point to show the view
     */
    public void show(Point point) {
        if (mActions == null) {
            throw new IllegalStateException("You need to give the QuickActionView actions before calling show!");
        }
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        params.x = point.x;
        params.y = point.y;
        mInnerCirclePoint = point;
        setPivotX(point.x);
        setPivotY(point.y);
        setScaleX(0);
        setScaleY(0);
        setAlpha(0);
        setVisibility(View.VISIBLE);
        setEnabled(true);
        animate().scaleX(1).scaleY(1).alpha(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        float angle = (float) Math.toDegrees(Math.atan2(0 - point.y, 0 - point.x));
        if (angle < 0) {
            angle += 360;
        }
        setStartAngle(angle);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.format = PixelFormat.TRANSLUCENT;

        manager.addView(this, params);
        if (mListener != null) {
            mListener.onQuickActionShow();
        }
    }

    /**
     * Dismiss the QuickActionView
     */
    public void dismiss() {
        AnimationSequencer sequencer = new AnimationSequencer();
        setEnabled(false);
        animate().scaleX(0).scaleY(0).alpha(0).setListener(sequencer).setDuration(350).start();
        sequencer.after(new Runnable() {
            @Override
            public void run() {
                WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                QuickActionView.this.setVisibility(View.GONE);
                manager.removeView(QuickActionView.this);
            }
        });
    }

    /**
     * Set the size of all actions text
     * @param size the size of text, in pixels
     */
    public QuickActionView setTextSize(float size) {
        mTextPaint.setTextSize(size);
        return this;
    }

    /**
     * Get the size of all actions text
     * @return the size of the text, in pixels
     */
    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    /**
     * Set the color of all actions text.
     * @param color the color of the text
     */
    public QuickActionView setTextColor(int color) {
        mDefaultQuickActionConfig.mTextColor = color;
        return this;
    }

    /**
     * Get the color of all actions text.
     * @return color of all text actions
     */
    public int getTextColor() {
        return mDefaultQuickActionConfig.getTextColor();
    }

    /**
     * Set the typeface for the text
     * See {@link Paint#setTypeface(Typeface)}
     * @param typeface the typeface
     */
    public QuickActionView setTextTypeface(Typeface typeface) {
        mTextPaint.setTypeface(typeface);
        return this;
    }

    /**
     * Get the typeface of the actions text
     * @return the typeface currently applied to the actions
     */
    public Typeface getTextTypeface() {
        return mTextPaint.getTypeface();
    }

    /**
     * Set the background color of all actions text.
     * @param color the color of the text
     */
    public QuickActionView setTextBackgroundColor(int color) {
        mDefaultQuickActionConfig.mTextBackgroundColor = color;
        return this;
    }

    /**
     * Get the background color of all actions text.
     * @return background color of all text actions
     */
    public int getTextBackgroundColor() {
        return mDefaultQuickActionConfig.getTextBackgroundColor();
    }

    /**
     * Set the angle where you would like the actions to start
     * @param angle starting angle, in degrees
     */
    public QuickActionView setStartAngle(float angle) {
        mStartAngle = angle;
        return this;
    }

    /**
     * Get the angle where actions will start displaying
     * @return angle where the actions start from, in degrees
     */
    public float getStartAngle() {
        return mStartAngle;
    }

    /**
     * Set the color filter for the actions when they are in the normal state
     * @param normalColorFilter color filter for the normal state
     */
    public QuickActionView setIconNormalColorFilter(ColorFilter normalColorFilter) {
        mDefaultQuickActionConfig.mNormalColorFilter = normalColorFilter;
        return this;
    }

    /**
     * Get the normal color filter
     * @return the color filter for the icon normal state
     */
    public @Nullable ColorFilter getIconNormalColorFilter() {
        return mDefaultQuickActionConfig.getNormalColorFilter();
    }

    /**
     * Set the color filter for the actions when they are in the pressed state
     * @param pressedColorFilter color filter for the pressed state
     */
    public QuickActionView setIconPressedColorFilter(ColorFilter pressedColorFilter) {
        mDefaultQuickActionConfig.mPressedColorFilter = pressedColorFilter;
        return this;
    }

    /**
     * Get the pressed color filter
     * @return the color filter for the icon pressed state
     */
    public @Nullable ColorFilter getIconPressedColorFilter() {
        return mDefaultQuickActionConfig.getPressedColorFilter();
    }

    /**
     * Set the color for the action icon background when they are in the pressed state
     * @param pressedColor background color for the pressed state
     */
    public QuickActionView setIconBackgroundPressedColor(@ColorInt int pressedColor) {
        mDefaultQuickActionConfig.mPressedBackgroundColor = pressedColor;
        return this;
    }

    /**
     * Get the pressed color for the icon background
     * @return the color for the icon background pressed state
     */
    public @ColorInt int getIconBackgroundPressedColor() {
        return mDefaultQuickActionConfig.getPressedBackgroundColor();
    }

    /**
     * Set the color for the action icon background when they are in the normal state
     * @param normalColor background color for the normal state
     */
    public QuickActionView setIconBackgroundNormalColor(@ColorInt int normalColor) {
        mDefaultQuickActionConfig.mNormalBackgroundColor = normalColor;
        return this;
    }

    /**
     * Get the normal color for the icon background
     * @return the color for the icon background normal state
     */
    public @ColorInt int getIconBackgroundNormalColor() {
        return mDefaultQuickActionConfig.getNormalBackgroundColor();
    }

    /**
     * Set the actions that populate the QuickActionView.
     * @param id the menu resource to populate the QuickActionView with
     */
    public QuickActionView setActions(@MenuRes int id) {
        mActions = new PopupMenu(getContext(), this);
        mActions.inflate(id);
        return this;
    }

    /**
     * Give a configuration to the specific action. See {@link QuickActionConfig} for more
     * @param actionId the id of the menu item to apply to config to
     * @param config the config for the menu item
     */
    public QuickActionView setQuickActionConfig(@IdRes int actionId, QuickActionConfig config) {
        mQuickActionConfigHashMap.put(actionId, config);
        return this;
    }

    /**
     * Get the {@link QuickActionConfig} for a specified actionId
     * @param actionId the actionId (menu item resource id)
     * @return the {@link QuickActionConfig} for the menu item or null if non exists
     */
    public @Nullable QuickActionConfig getQuickActionConfig(@IdRes int actionId) {
        return mQuickActionConfigHashMap.get(actionId);
    }

    /**
     * Sets the color of the scrim (the background behind the quick actions)
     * @param scrimColor the color you want the scrim to be (please use semi transparent colors). Set to {@link Color#TRANSPARENT} to remove scrim
     */
    public QuickActionView setScrimColor(@ColorInt int scrimColor) {
        mScrimColor = scrimColor;
        return this;
    }

    /**
     * Set the color of the circle that appears where the view was long pressed
     * @param circleColor the color you want the circle to be
     */
    public QuickActionView setTouchCircleColor(@ColorInt int circleColor) {
        mTouchCirclePaint.setColor(circleColor);
        return this;
    }

    /**
     * Get the touch circles color
     * @return the color of the touch circle
     */
    public int getTouchCircleColor() {
        return mTouchCirclePaint.getColor();
    }

    /**
     * Set the mode of the circle, one of {@link com.ovenbits.quickactionview.QuickActionView.CircleMode}
     * either {@link #CIRCLE_MODE_FILL} (solid circle)
     * or {@link #CIRCLE_MODE_STROKE} (just the outline of a circle)
     * @param circleMode the mode you want your circle to be
     */
    public QuickActionView setCircleMode(@CircleMode int circleMode) {
        switch (circleMode) {
            case CIRCLE_MODE_FILL:
                mTouchCirclePaint.setStyle(Paint.Style.FILL);
                break;
            case CIRCLE_MODE_STROKE:
                mTouchCirclePaint.setStyle(Paint.Style.STROKE);
                break;
        }
        return this;
    }

    /**
     * Sets the stroke width of the circle. Only relavent if the circle is set to {@link #CIRCLE_MODE_STROKE}
     * See {@link Paint#setStrokeWidth(float)}
     * @param pixelWidth the width of the stroke in pixels. Pass 0 for hairline
     */
    public QuickActionView setCircleStrokeWidth(int pixelWidth) {
        mTouchCirclePaint.setStrokeWidth(pixelWidth);
        return this;
    }

    /**
     * Set a listener, which can detect when actions are performed on the
     * QuickActionView
     * @param listener the listener you want to recieve the events
     */
    public QuickActionView setQuickActionListener(OnQuickActionSelectedListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mLastTouchY = y;
                    mLastTouchX = x;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    MenuItem action = getOverlappingQuickAction(x, y);
                    if (action != null && mListener != null) {
                        mListener.onQuickActionSelected(this, action.getItemId());
                    }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    Point point = mInnerCirclePoint;
                    mLastTouchX = point.x;
                    mLastTouchY = point.y;
                    if (mListener != null) {
                        mListener.onDismiss();
                    }
                    dismiss();
            }
        }
        return isEnabled();
    }

    private MenuItem getOverlappingQuickAction(float x, float y) {
        for (int i = 0; i < mActions.getMenu().size(); i++) {
            Point point = getActionPoint(i);
            if (distance(point, x, y) < mActionCircleRadiusExpanded) {
                return mActions.getMenu().getItem(i);
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mScrimColor != Color.TRANSPARENT) {
            canvas.drawColor(mScrimColor);
        }

        Point innerCircle = mInnerCirclePoint;

        canvas.drawCircle(innerCircle.x, innerCircle.y, mTouchCircleRadius, mTouchCirclePaint);

        for (int i = 0; i < mActions.getMenu().size(); i++) {
            MenuItem item = mActions.getMenu().getItem(i);
            QuickActionConfig config = mQuickActionConfigHashMap.get(item.getItemId());
            if (config == null) {
                config = mDefaultQuickActionConfig;
            }

            Point point = getActionPoint(i);

            float distance = (distance(point, mLastTouchX, mLastTouchY));
            float interpol = Math.min(1, Math.max(0, 1 - (distance / (mActionCircleRadius + mLineDistance))));
            float radius = mActionCircleRadius + (mActionCircleRadiusExpanded - mActionCircleRadius) * interpol;
            boolean isPressed = insideCircle(point, mActionCircleRadius, mLastTouchX, mLastTouchY);
            if (isPressed) {
                mActionPaint.setColor(config.getPressedBackgroundColor());
            } else {
                mActionPaint.setColor(config.getNormalBackgroundColor());
            }
            mActionPaint.setShadowLayer(radius / 5, 0, (float) (radius - (mActionCircleRadius * .9)), Color.parseColor("#50000000"));

            canvas.drawCircle(point.x, point.y, radius, mActionPaint);
            Drawable icon = item.getIcon();

            if (icon != null) {
                if (isPressed) {
                    icon.setColorFilter(config.getPressedColorFilter());
                } else {
                    icon.setColorFilter(config.getNormalColorFilter());
                }
                Rect bounds = getRectInsideCircle(point, radius);
                bounds.inset((int) mTextBackgroundPadding, (int) mTextBackgroundPadding);

                float aspect = icon.getIntrinsicWidth() / (float) (icon.getIntrinsicHeight());
                int desiredWidth = (int) Math.min(bounds.width(), bounds.height() * aspect);
                int desiredHeight = (int) Math.min(bounds.height(), bounds.width() / aspect);

                bounds.inset((bounds.width() - desiredWidth) / 2, (bounds.height() - desiredHeight) / 2);
                icon.setBounds(bounds);
                icon.draw(canvas);
            }

            if (isPressed) {
                mTextBorderPaint.setColor(config.getTextBackgroundColor());
                mTextPaint.setColor(config.getTextColor());
                Point text = getCircleEdgePoint(innerCircle, mTouchCircleRadius + mLineDistance + mActionCircleRadiusExpanded + mTextPadding + mTextBackgroundPadding, (float) Math.toDegrees(Math.atan2(point.y - innerCircle.y, point.x - innerCircle.x)));
                float measure = mTextPaint.measureText(item.getTitle().toString());
                mTextRect.set(text.x, text.y - mTextPaint.getTextSize(), text.x + measure, text.y + mTextPaint.descent());
                mTextRect.inset(-mTextBackgroundPadding, -mTextBackgroundPadding);
                canvas.drawRoundRect(mTextRect, mTextPadding, mTextPadding, mTextBorderPaint);

                canvas.drawText(item.getTitle().toString(), text.x, text.y, mTextPaint);
            }
        }
    }

    private boolean insideCircle(Point center, float radius, float x, float y) {
        return distance(center, x, y) < radius;
    }

    private float distance(Point point, float x, float y) {
        return (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
    }

    private Rect getRectInsideCircle(Point center, float radius) {
        Rect rect = new Rect(0, 0, (int) ((radius * 2) / Math.sqrt(2)), (int) ((radius * 2) / Math.sqrt(2)));
        rect.offsetTo(center.x - rect.width() / 2, center.y - rect.width() / 2);
        return rect;
    }

    private Point getCircleEdgePoint(Point center, float radius, float angle) {
        float rads = (float) Math.toRadians(angle);
        Point ret = new Point(center);
        ret.offset((int) (Math.cos(rads) * radius), (int) (Math.sin(rads) * radius));
        return ret;
    }

    private Point getActionPoint(int index) {
        float angle = (float) (Math.toRadians(mStartAngle) + index * 2 * (Math.atan2(mActionCircleRadiusExpanded + mAngularSpacing, mTouchCircleRadius + mLineDistance)));
        Point point = new Point(mInnerCirclePoint);
        point.offset((int) (Math.cos(angle) * (mTouchCircleRadius + mLineDistance)), (int) (Math.sin(angle) * (mTouchCircleRadius + mLineDistance)));
        return point;
    }
}