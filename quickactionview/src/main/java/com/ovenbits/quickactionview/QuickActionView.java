package com.ovenbits.quickactionview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;

/**
 * QuickActionView, which shows actions emanating from the location where a user long presses.
 * Created by Alex on 6/12/15.
 */
public class QuickActionView extends View {

    private static final int DEFAULT_RADIUS = 40;
    private static final int DEFAULT_DISTANCE = 70;
    private static final int DEFAULT_ACTION_RADIUS = 25;
    private static final int DEFAULT_ACTION_RADIUS_EXPANDED = 30;
    private static final int DEFAULT_TEXT_PADDING = 8;
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_TEXT_BACKGROUND_PADDING = 4;
    private static final int DEFAULT_BORDER = 2;

    private int mLineThickness = 1;
    private int mLineColor = Color.parseColor("#CCFFFFFF");
    private float mLineDistance;
    private float mInnerCircleRadius;
    private int mInnerCircleColor = Color.parseColor("#CCFFFFFF");
    private float mActionCircleRadius;
    private float mActionCircleRadiusExpanded;
    private float mStartAngle = 270;
    private float mAngularSpacing = 15;

    private int mNormalIconColor = Color.WHITE;
    private int mActionIconColor = Color.WHITE;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private float mTextPadding = 0;
    private float mTextSize = 0;
    private float mTextBackgroundPadding;
    private float mBorderWidth;

    private PopupMenu mActions;
    private HashMap<Integer, QuickActionConfig> mQuickActionConfigHashMap;
    private QuickActionConfig mDefaultQuickActionConfig;
    private OnQuickActionSelectedListener mListener;

    private Paint mCirclePaint;
    private Paint mLinePaint;
    private Paint mActionPaint;
    private Paint mActionBorderPaint;
    private Paint mTextPaint;
    private Paint mTextBorderPaint;

    private Point mInnerCirclePoint = new Point();

    public QuickActionView(Context context) {
        super(context);
        init();
    }

    public void show(Context context, View origin, Point offset) {
        int[] loc = new int[2];
        origin.getLocationInWindow(loc);

        Point point = new Point(offset);
        point.offset(loc[0], loc[1]);
        show(context, point);
    }

    public void show(Context context, Point point) {
        if (mActions == null) {
            throw new IllegalStateException("You should give the QuickActionView actions before calling show!");
        }
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        params.x = point.x;
        params.y = point.y;
        setInnerCirclePoint(point);
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
    }

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

    private void init() {
        mQuickActionConfigHashMap = new HashMap<>();
        mDefaultQuickActionConfig = QuickActionConfig.getDefaultConfig(getContext());
        setVisibility(View.GONE);
        setClickable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //Eventually put these in the dimens file and make them changeable attributes to
        mActionCircleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS, getResources().getDisplayMetrics());
        mActionCircleRadiusExpanded = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS_EXPANDED, getResources().getDisplayMetrics());
        mLineDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DISTANCE, getResources().getDisplayMetrics());
        mInnerCircleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ACTION_RADIUS, getResources().getDisplayMetrics());
        mTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_PADDING, getResources().getDisplayMetrics());
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics());
        mTextBackgroundPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_BACKGROUND_PADDING, getResources().getDisplayMetrics());
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BORDER, getResources().getDisplayMetrics());

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mInnerCircleColor);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineThickness);
        mLinePaint.setColor(mLineColor);
        mActionPaint = new Paint();
        mActionPaint.setAntiAlias(true);
        mActionPaint.setStyle(Paint.Style.FILL);
        mActionBorderPaint = new Paint();
        mActionBorderPaint.setStrokeWidth(mBorderWidth);
        mActionBorderPaint.setAntiAlias(true);
        mActionBorderPaint.setColor(Color.WHITE);
        mActionBorderPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);

        mTextBorderPaint = new Paint();
        mTextBorderPaint.setAntiAlias(true);
        mTextBorderPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    public void setStartAngle(float angle) {
        this.mStartAngle = angle;
    }

    public void setNormalIconColor(int normalIconColor) {
        mNormalIconColor = normalIconColor;
    }

    public void setActionIconColor(int actionIconColor) {
        mActionIconColor = actionIconColor;
    }

    public Point getInnerCirclePoint() {
        return mInnerCirclePoint;
    }

    public void setInnerCirclePoint(Point innerCirclePoint) {
        mInnerCirclePoint = innerCirclePoint;
    }

    private Point getActionPoint(int index) {
        float angle = (float) (Math.toRadians(mStartAngle) + index * 2 * (Math.atan2(mActionCircleRadiusExpanded + mAngularSpacing, mInnerCircleRadius + mLineDistance)));
        Point point = new Point(getInnerCirclePoint());
        point.offset((int) (Math.cos(angle) * (mInnerCircleRadius + mLineDistance)), (int) (Math.sin(angle) * (mInnerCircleRadius + mLineDistance)));
        return point;
    }

    private float getIconSize(int radius) {
        return (float) ((radius * 2) * Math.sqrt(2));
    }

    public void setActions(PopupMenu actions) {
        mActions = actions;
    }

    public void setActions(@MenuRes int id) {
        mActions = new PopupMenu(getContext(), this);
        mActions.inflate(id);
    }

    /**
     * Give a configuration to the specific action. See {@link QuickActionConfig} for more
     * @param actionId the id of the menu item to apply to config to
     * @param config the config for the menu item
     */
    public void setQuickActionConfig(int actionId, QuickActionConfig config) {
        mQuickActionConfigHashMap.put(actionId, config);
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
                    Point point = getInnerCirclePoint();
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

        Point innerCircle = getInnerCirclePoint();

        canvas.drawCircle(innerCircle.x, innerCircle.y, mInnerCircleRadius, mCirclePaint);

        for (int i = 0; i < mActions.getMenu().size(); i++) {
            MenuItem item = mActions.getMenu().getItem(i);
            QuickActionConfig config = mQuickActionConfigHashMap.get(item.getItemId());
            if (config == null) {
                config = mDefaultQuickActionConfig;
            }

            Point point = getActionPoint(i);

            mLinePaint.setColor(mLineColor);
            mLinePaint.setStrokeWidth(mLineThickness);
            ColorFilter colorFilter;

            float distance = (distance(point, mLastTouchX, mLastTouchY));
            float interpol = Math.min(1, Math.max(0, 1 - (distance / (mActionCircleRadius + mLineDistance))));
            float radius = mActionCircleRadius + (mActionCircleRadiusExpanded - mActionCircleRadius) * interpol;
            boolean drawText = false;
            if (insideCircle(point, mActionCircleRadius, mLastTouchX, mLastTouchY)) {
                drawText = true;
                colorFilter = new PorterDuffColorFilter(mActionIconColor, PorterDuff.Mode.SRC_IN);
            } else {
                colorFilter = new PorterDuffColorFilter(mNormalIconColor, PorterDuff.Mode.SRC_IN);
            }
            if (config != null) {
                mActionPaint.setColor(config.getBackgroundColor());
            }
            mActionPaint.setShadowLayer(radius / 5, 0, (float) (radius - (mActionCircleRadius * .9)), Color.parseColor("#50000000"));

            canvas.drawCircle(point.x, point.y, radius, mActionPaint);
            canvas.drawCircle(point.x, point.y, radius, mActionBorderPaint);
            Drawable icon = item.getIcon();
            //Do color filtering

            icon.setColorFilter(colorFilter);
            Rect bounds = getRectInsideCircle(point, radius);
            bounds.inset((int) mTextBackgroundPadding, (int) mTextBackgroundPadding);

            float aspect = icon.getIntrinsicWidth() / (float) (icon.getIntrinsicHeight());
            int desiredWidth = (int) Math.min(bounds.width(), bounds.height() * aspect);
            int desiredHeight = (int) Math.min(bounds.height(), bounds.width() / aspect);

            bounds.inset((bounds.width() - desiredWidth) / 2, (bounds.height() - desiredHeight) / 2);
            icon.setBounds(bounds);
            icon.draw(canvas);

            if (drawText) {
                if (config != null) {
                    mTextBorderPaint.setColor(config.getTextBackgroundColor());
                    mTextPaint.setColor(config.getTextColor());
                }
                Point text = getCircleEdgePoint(innerCircle, mInnerCircleRadius + mLineDistance + mActionCircleRadiusExpanded + mTextPadding + mTextBackgroundPadding, (float) Math.toDegrees(Math.atan2(point.y - innerCircle.y, point.x - innerCircle.x)));
                float measure = mTextPaint.measureText(item.getTitle().toString());
                RectF rect = new RectF(text.x, text.y - mTextSize, text.x + measure, text.y + mTextPaint.descent());
                rect.inset(-mTextBackgroundPadding, -mTextBackgroundPadding);
                canvas.drawRoundRect(rect, mTextPadding, mTextPadding, mTextBorderPaint);

                //text.offset(0, (int) -mTextPaint.getTextSize());
                canvas.drawText(item.getTitle().toString(), text.x, text.y, mTextPaint);
            }
        }
    }

    public void setQuickActionListener(OnQuickActionSelectedListener listener) {
        mListener = listener;
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

    public interface OnQuickActionSelectedListener {
        void onQuickActionSelected(View view, int action);

        void onDismiss();
    }

}