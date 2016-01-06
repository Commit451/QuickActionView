package com.ovenbits.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A QuickActionView, which shows actions when a view is long pressed.
 * <p/>
 * https://github.com/ovenbits/QuickActionView
 */
public class QuickActionView {

    private boolean mShown = false;
    private Context mContext;
    private OnActionSelectedListener mOnActionSelectedListener;
    private OnDismissListener mOnDismissListener;
    private OnShowListener mOnShowListener;
    private Float mStartAngle;
    private float mActionDistance;
    private int mActionPadding;
    private ArrayList<Action> mActions = new ArrayList<>();
    private Bundle mExtras;
    private QuickActionViewLayout mQuickActionViewLayout;
    private Config mConfig;
    private ActionsInAnimator mActionsInAnimator;
    private ActionsOutAnimator mActionsOutAnimator;


    @ColorInt
    private int mScrimColor = Color.parseColor("#99000000");
    private Drawable mIndicatorDrawable;
    private HashMap<View, RegisteredListener> mRegisteredListeners = new HashMap<>();

    private View mClickedView;

    private QuickActionView(Context context) {
        mContext = context;
        mConfig = new Config(context);
        mIndicatorDrawable = ContextCompat.getDrawable(context, R.drawable.qav_indicator);
        mActionDistance = context.getResources().getDimensionPixelSize(R.dimen.qav_action_distance);
        mActionPadding = context.getResources().getDimensionPixelSize(R.dimen.qav_action_padding);
        DefaultAnimator defaultAnimator = new DefaultAnimator();
        mActionsInAnimator = defaultAnimator;
        mActionsOutAnimator = defaultAnimator;
    }

    public static QuickActionView make(Context context) {
        return new QuickActionView(context);
    }

    private void show(View anchor, Point offset) {
        if (mShown) {
            throw new RuntimeException("Show cannot be called when the QuickActionView is already visible");
        }
        mShown = true;

        ViewParent parent = anchor.getParent();
        if (parent instanceof View) {
            parent.requestDisallowInterceptTouchEvent(true);
        }

        mClickedView = anchor;

        int[] loc = new int[2];
        anchor.getLocationInWindow(loc);
        Point point = new Point(offset);
        point.offset(loc[0], loc[1]);
        display(point);
    }


    public QuickActionView register(View view) {
        RegisteredListener listener = new RegisteredListener();
        mRegisteredListeners.put(view, listener);
        view.setOnTouchListener(listener);
        view.setOnLongClickListener(listener);
        return this;
    }

    public void unregister(View view) {
        mRegisteredListeners.remove(view);
        view.setOnTouchListener(null);
        view.setOnLongClickListener(null);
    }

    public QuickActionView addAction(Action action) {
        checkShown();
        mActions.add(action);
        return this;
    }

    public QuickActionView addActions(Collection<Action> actions) {
        checkShown();
        mActions.addAll(actions);
        return this;
    }

    /**
     * Add actions to the QuickActionView from the given menu resource id.
     *
     * @param menuId menu resource id
     * @return the QuickActionView
     */
    public QuickActionView addActions(@MenuRes int menuId) {
        Menu menu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(menuId, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Action action = new Action(item.getItemId(), item.getIcon(), item.getTitle());
            addAction(action);
        }
        return this;
    }

    public QuickActionView clearActions() {
        mActions.clear();
        return this;
    }

    public QuickActionView setOnActionSelectedListener(OnActionSelectedListener onActionSelectedListener) {
        mOnActionSelectedListener = onActionSelectedListener;
        return this;
    }

    public QuickActionView setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public QuickActionView setOnShowListener(OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
        return this;
    }

    public void setStartAngle(Float startAngle) {
        checkShown();
        mStartAngle = startAngle;
    }

    public QuickActionView setIndicatorDrawable(Drawable indicatorDrawable) {
        mIndicatorDrawable = indicatorDrawable;
        return this;
    }

    public QuickActionView setScrimColor(@ColorInt int scrimColor) {
        mScrimColor = scrimColor;
        return this;
    }


    public QuickActionView setTextBackgroundDrawable(@DrawableRes int textBackgroundDrawable) {
        mConfig.setTextBackgroundDrawable(textBackgroundDrawable);
        return this;
    }


    public QuickActionView setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
        mConfig.setBackgroundColorStateList(backgroundColorStateList);
        return this;
    }

    public QuickActionView setTextColor(@ColorInt int textColor) {
        mConfig.setTextColor(textColor);
        return this;
    }

    public QuickActionView setBackgroundColor(@ColorInt int backgroundColor) {
        mConfig.setBackgroundColor(backgroundColor);
        return this;
    }


    public QuickActionView setTypeface(Typeface typeface) {
        mConfig.setTypeface(typeface);
        return this;
    }

    public QuickActionView setTextSize(int textSize) {
        mConfig.setTextSize(textSize);
        return this;
    }

    public QuickActionView setTextPaddingTop(int textPaddingTop) {
        mConfig.setTextPaddingTop(textPaddingTop);
        return this;
    }

    public QuickActionView setTextPaddingBottom(int textPaddingBottom) {
        mConfig.setTextPaddingBottom(textPaddingBottom);
        return this;
    }

    public QuickActionView setTextPaddingLeft(int textPaddingLeft) {
        mConfig.setTextPaddingLeft(textPaddingLeft);
        return this;
    }

    public QuickActionView setTextPaddingRight(int textPaddingRight) {
        mConfig.setTextPaddingRight(textPaddingRight);
        return this;
    }

    /**
     * Override the animations for when the QuickActionView shows
     *
     * @param actionsInAnimator the animation overrides
     * @return this QuickActionView
     */
    public QuickActionView setActionsInAnimator(ActionsInAnimator actionsInAnimator) {
        mActionsInAnimator = actionsInAnimator;
        return this;
    }

    /**
     * Override the animations for when the QuickActionView dismisses
     *
     * @param actionsOutAnimator the animation overrides
     * @return this QuickActionView
     */
    public QuickActionView setActionsOutAnimator(ActionsOutAnimator actionsOutAnimator) {
        mActionsOutAnimator = actionsOutAnimator;
        return this;
    }

    /**
     * Set a custom configuration for the action with the given id
     *
     * @param config   the configuration to attach
     * @param actionId the action id
     * @return this QuickActionView
     */
    public QuickActionView setActionConfig(Action.Config config, @IdRes int actionId) {
        for (Action action : mActions) {
            if (action.getId() == actionId) {
                action.setConfig(config);
                return this;
            }
        }

        throw new IllegalArgumentException("No Action exists with id " + actionId);
    }

    private void display(Point point) {
        if (mActions == null) {
            throw new IllegalStateException("You need to give the QuickActionView actions before calling show!");
        }

        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        params.format = PixelFormat.TRANSLUCENT;
        mQuickActionViewLayout = new QuickActionViewLayout(mContext, mActions, point);
        manager.addView(mQuickActionViewLayout, params);
        if (mOnShowListener != null) {
            mOnShowListener.onShow(this);
        }
    }

    private void animateHide() {
        int duration = mQuickActionViewLayout.animateOut();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView();
            }
        }, duration);
    }

    private void removeView() {

        if (mQuickActionViewLayout != null) {
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            manager.removeView(mQuickActionViewLayout);
            mQuickActionViewLayout = null;
            mShown = false;
        }

        if (mClickedView != null) {
            ViewParent parent = mClickedView.getParent();
            if (parent instanceof View) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }
    }

    private void dismiss() {
        if (!mShown) {
            throw new RuntimeException("The QuickActionView must be visible to call dismiss()");
        }
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(QuickActionView.this);
        }
        animateHide();
    }

    private void checkShown() {
        if (mShown) {
            throw new RuntimeException("QuickActionView cannot be configured if show has already been called.");
        }
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public QuickActionView setExtras(Bundle extras) {
        mExtras = extras;
        return this;
    }

    public View getClickedView() {
        return mClickedView;
    }

    public interface OnActionSelectedListener {
        void onActionSelected(Action action, QuickActionView quickActionView);
    }

    public interface OnDismissListener {
        void onDismiss(QuickActionView quickActionView);
    }

    public interface OnShowListener {
        void onShow(QuickActionView quickActionView);
    }

    public static class Config {
        private Action.Config mDefaultConfig;
        private Typeface mTypeface;
        private int mTextSize;
        private int mTextPaddingTop;
        private int mTextPaddingBottom;
        private int mTextPaddingLeft;
        private int mTextPaddingRight;

        public Config(Context context) {
            this(context, null, context.getResources().getInteger(R.integer.qav_action_title_view_text_size), context.getResources().getDimensionPixelSize(R.dimen.qav_action_title_view_text_padding));
        }

        private Config(Context context, Typeface typeface, int textSize, int textPadding) {
            mTypeface = typeface;
            mTextSize = textSize;
            mTextPaddingTop = textPadding;
            mTextPaddingBottom = textPadding;
            mTextPaddingLeft = textPadding;
            mTextPaddingRight = textPadding;
            mDefaultConfig = new Action.Config(context);
        }


        public Drawable getTextBackgroundDrawable(Context context) {
            return mDefaultConfig.getTextBackgroundDrawable(context);
        }

        public void setTextBackgroundDrawable(@DrawableRes int textBackgroundDrawable) {
            mDefaultConfig.setTextBackgroundDrawable(textBackgroundDrawable);
        }

        public int getTextColor() {
            return mDefaultConfig.getTextColor();
        }

        public void setTextColor(@ColorInt int textColor) {
            mDefaultConfig.setTextColor(textColor);
        }


        public ColorStateList getBackgroundColorStateList() {
            return mDefaultConfig.getBackgroundColorStateList();
        }

        public void setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
            mDefaultConfig.setBackgroundColorStateList(backgroundColorStateList);
        }

        public void setBackgroundColor(@ColorInt int backgroundColor) {
            mDefaultConfig.setBackgroundColor(backgroundColor);
        }


        public Typeface getTypeface() {
            return mTypeface;
        }

        public void setTypeface(Typeface typeface) {
            mTypeface = typeface;
        }

        public int getTextSize() {
            return mTextSize;
        }

        public void setTextSize(int textSize) {
            mTextSize = textSize;
        }

        public int getTextPaddingTop() {
            return mTextPaddingTop;
        }

        public void setTextPaddingTop(int textPaddingTop) {
            mTextPaddingTop = textPaddingTop;
        }

        public int getTextPaddingBottom() {
            return mTextPaddingBottom;
        }

        public void setTextPaddingBottom(int textPaddingBottom) {
            mTextPaddingBottom = textPaddingBottom;
        }

        public int getTextPaddingLeft() {
            return mTextPaddingLeft;
        }

        public void setTextPaddingLeft(int textPaddingLeft) {
            mTextPaddingLeft = textPaddingLeft;
        }

        public int getTextPaddingRight() {
            return mTextPaddingRight;
        }

        public void setTextPaddingRight(int textPaddingRight) {
            mTextPaddingRight = textPaddingRight;
        }
    }

    private static class DefaultAnimator implements ActionsInAnimator, ActionsOutAnimator {
        private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();

        @Override
        public void animateActionIn(Action action, int position, ActionView view, Point center) {
            view.setScaleX(0.1f);
            view.setScaleY(0.1f);
            view.animate().scaleY(1.0f)
                    .scaleX(1.0f)
                    .setDuration(200)
                    .setInterpolator(mOvershootInterpolator);
        }

        @Override
        public void animateIndicatorIn(View indicator) {
            indicator.setAlpha(0);
            indicator.animate().alpha(1).setDuration(200);
        }

        @Override
        public void animateScrimIn(View scrim) {
            scrim.setAlpha(0f);
            scrim.animate().alpha(1f).setDuration(200);
        }

        @Override
        public int animateActionOut(Action action, int position, ActionView view, Point center) {
            view.animate().scaleX(0.1f)
                    .scaleY(0.1f)
                    .alpha(0.0f)
                    .setStartDelay(0)
                    .setDuration(200);
            return 200;
        }

        @Override
        public int animateIndicatorOut(View indicator) {
            indicator.animate().alpha(0).setDuration(200);
            return 200;
        }

        @Override
        public int animateScrimOut(View scrim) {
            scrim.animate().alpha(0).setDuration(200);
            return 200;
        }
    }

    protected class QuickActionViewLayout extends FrameLayout {

        private Point mCenterPoint;
        private View mIndicatorView;
        private View mScrimView;
        private LinkedHashMap<Action, ActionView> mActionViews = new LinkedHashMap<>();
        private LinkedHashMap<Action, ActionTitleView> mActionTitleViews = new LinkedHashMap<>();
        private PointF mLastTouch = new PointF();
        private boolean mAnimated = false;

        public QuickActionViewLayout(Context context, ArrayList<Action> actions, Point centerPoint) {
            super(context);
            mCenterPoint = centerPoint;
            mScrimView = new View(context);
            mScrimView.setBackgroundColor(mScrimColor);
            FrameLayout.LayoutParams scrimParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mScrimView, scrimParams);
            mIndicatorView = new View(context);
            if (Build.VERSION.SDK_INT >= 16) {
                mIndicatorView.setBackground(mIndicatorDrawable);
            } else {
                mIndicatorView.setBackgroundDrawable(mIndicatorDrawable);
            }
            FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(mIndicatorDrawable.getIntrinsicWidth(), mIndicatorDrawable.getIntrinsicHeight());
            addView(mIndicatorView, indicatorParams);
            for (Action action : actions) {
                ConfigHelper helper = new ConfigHelper(action.getConfig(), mConfig);
                ActionView actionView = new ActionView(context, action, helper);
                mActionViews.put(action, actionView);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addView(actionView, params);
                ActionTitleView actionTitleView = new ActionTitleView(context, action, helper);
                actionTitleView.setVisibility(View.GONE);
                mActionTitleViews.put(action, actionTitleView);
                FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addView(actionTitleView, titleParams);
            }
        }


        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            mScrimView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

            mIndicatorView.layout(mCenterPoint.x - (int) (mIndicatorView.getMeasuredWidth() / 2.0),
                    mCenterPoint.y - (int) (mIndicatorView.getMeasuredHeight() / 2.0),
                    mCenterPoint.x + (int) (mIndicatorView.getMeasuredWidth() / 2.0),
                    mCenterPoint.y + (int) (mIndicatorView.getMeasuredHeight() / 2.0));
            int index = 0;
            for (Map.Entry<Action, ActionView> entry : mActionViews.entrySet()) {
                ActionView actionView = entry.getValue();
                PointF point = getActionPoint(index, actionView);
                point.offset(-actionView.getCircleCenterX(), -actionView.getCircleCenterY());
                actionView.layout((int) point.x, (int) point.y, (int) (point.x + actionView.getMeasuredWidth()), (int) (point.y + actionView.getMeasuredHeight()));
                ActionTitleView titleView = mActionTitleViews.get(entry.getKey());
                float titleLeft = point.x + (actionView.getMeasuredWidth() / 2) - (titleView.getMeasuredWidth() / 2);
                float titleTop = point.y - 10 - titleView.getMeasuredHeight();
                titleView.layout((int) titleLeft, (int) titleTop, (int) (titleLeft + titleView.getMeasuredWidth()), (int) (titleTop + titleView.getMeasuredHeight()));
                index++;
            }

            if (!mAnimated) {
                animateActionsIn();
                animateIndicatorIn();
                animateScrimIn();
                mAnimated = true;
            }
        }

        private void animateActionsIn() {
            int index = 0;
            for (ActionView view : mActionViews.values()) {
                mActionsInAnimator.animateActionIn(view.getAction(), index, view, mCenterPoint);
                index++;
            }
        }

        private void animateIndicatorIn() {
            mActionsInAnimator.animateIndicatorIn(mIndicatorView);
        }

        private void animateScrimIn() {
            mActionsInAnimator.animateScrimIn(mScrimView);
        }

        int animateOut() {
            int maxDuration = 0;
            maxDuration = Math.max(maxDuration, animateActionsOut());
            maxDuration = Math.max(maxDuration, animateScrimOut());
            maxDuration = Math.max(maxDuration, animateIndicatorOut());
            return maxDuration;
        }

        private int animateActionsOut() {
            int index = 0;
            int maxDuration = 0;
            for (ActionView view : mActionViews.values()) {
                view.clearAnimation();
                maxDuration = Math.max(mActionsOutAnimator.animateActionOut(view.getAction(), index, view, mCenterPoint), maxDuration);
                index++;
            }
            return maxDuration;
        }

        private int animateIndicatorOut() {
            mIndicatorView.clearAnimation();
            return mActionsOutAnimator.animateIndicatorOut(mIndicatorView);
        }

        private int animateScrimOut() {
            mScrimView.clearAnimation();
            return mActionsOutAnimator.animateScrimOut(mScrimView);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mShown) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mLastTouch.set(event.getRawX(), event.getRawY());
                        int index = 0;
                        for (ActionView actionView : mActionViews.values()) {
                            if (insideCircle(getActionPoint(index, actionView), actionView.getActionCircleRadiusExpanded(), event.getRawX(), event.getRawY())) {
                                if (!actionView.isSelected()) {
                                    actionView.setSelected(true);
                                    actionView.animateInterpolation(1);
                                    mActionTitleViews.get(actionView.getAction()).setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (actionView.isSelected()) {
                                    actionView.setSelected(false);
                                    actionView.animateInterpolation(0);
                                    mActionTitleViews.get(actionView.getAction()).setVisibility(View.GONE);
                                }
                            }
                            index++;
                        }
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        for (Map.Entry<Action, ActionView> entry : mActionViews.entrySet()) {
                            if (entry.getValue().isSelected() && mOnActionSelectedListener != null) {
                                mOnActionSelectedListener.onActionSelected(entry.getKey(), QuickActionView.this);
                                break;
                            }
                        }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:

                        dismiss();
                }
            }
            return true;
        }


        private PointF getActionPoint(int index, ActionView view) {
            if (mStartAngle == null) {
                mStartAngle = 270f;
            }
            float angle = (float) (Math.toRadians(mStartAngle) + index * 2 * (Math.atan2(view.getActionCircleRadiusExpanded() + mActionPadding, mActionDistance)));
            PointF point = new PointF(mCenterPoint);
            point.offset((int) (Math.cos(angle) * (mIndicatorView.getWidth() + mActionDistance)), (int) (Math.sin(angle) * (mIndicatorView.getWidth() + mActionDistance)));
            return point;
        }

        private float getActionAngle(int index, ActionView view) {
            if (mStartAngle == null) {
                mStartAngle = 270f;
            }
            return (float) (Math.toRadians(mStartAngle) + index * 2 * (Math.atan2(view.getActionCircleRadiusExpanded() + mActionPadding, mActionDistance)));
        }

        private boolean insideCircle(PointF center, float radius, float x, float y) {
            return distance(center, x, y) < radius;
        }

        private float distance(PointF point, float x, float y) {
            return (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        }

    }

    private class RegisteredListener implements View.OnLongClickListener, View.OnTouchListener {

        private float mTouchX;
        private float mTouchY;

        @Override
        public boolean onLongClick(View v) {
            show(v, new Point((int) mTouchX, (int) mTouchY));
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mTouchX = event.getX();
            mTouchY = event.getY();
            if (mShown) {
                mQuickActionViewLayout.onTouchEvent(event);
            }
            return mShown;
        }
    }
}
