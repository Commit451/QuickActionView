package com.commit451.quickactionview;

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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.commit451.quickactionview.animator.FadeInFadeOutActionsTitleAnimator;
import com.commit451.quickactionview.animator.SlideFromCenterAnimator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A QuickActionView, which shows actions when a view is long pressed.
 *
 * @see <a href="https://github.com/Commit451/QuickActionView">https://github.com/Commit451/QuickActionView</a>
 */
public class QuickActionView {

    private boolean mShown = false;
    private boolean mDoesShowOnClick = false;
    private Context mContext;
    private OnActionSelectedListener mOnActionSelectedListener;
    private OnDismissListener mOnDismissListener;
    private OnShowListener mOnShowListener;
    private OnActionHoverChangedListener mOnActionHoverChangedListener;
    private float mActionDistance;
    private int mActionPadding;
    private ArrayList<Action> mActions = new ArrayList<>();
    private Bundle mExtras;
    private QuickActionViewLayout mQuickActionViewLayout;
    private Config mConfig;
    private ActionsInAnimator mActionsInAnimator;
    private ActionsOutAnimator mActionsOutAnimator;
    private ActionsTitleInAnimator mActionsTitleInAnimator;
    private ActionsTitleOutAnimator mActionsTitleOutAnimator;

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
        SlideFromCenterAnimator defaultAnimator = new SlideFromCenterAnimator(true);
        FadeInFadeOutActionsTitleAnimator defaultTitleAnimator = new FadeInFadeOutActionsTitleAnimator();
        mActionsInAnimator = defaultAnimator;
        mActionsOutAnimator = defaultAnimator;
        mActionsTitleInAnimator = defaultTitleAnimator;
        mActionsTitleOutAnimator = defaultTitleAnimator;
    }

    /**
     * Create a QuickActionView which you can configure as desired, then
     * call {@link #register(View)} to show it.
     *
     * @param context activity context
     * @return the QuickActionView for you to
     */
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


    /**
     * Register the QuickActionView to appear when the passed view is long pressed
     *
     * @param view the view to have long press responses
     * @return the QuickActionView
     */
    public QuickActionView register(View view) {
        RegisteredListener listener = new RegisteredListener();
        mRegisteredListeners.put(view, listener);
        view.setOnTouchListener(listener);
        if (mDoesShowOnClick) {
            view.setOnClickListener(listener);
        } else {
            view.setOnLongClickListener(listener);
        }
        return this;
    }


    /**
     * Set if the QuickActionView appears when the passed view is clicked
     *
     * @param showsOnclick boolean value, indicates if the QuickActionView shows on click instead of long pressed
     * @return the QuickActionView
     */
    public QuickActionView setDoesShowOnClick(boolean showsOnclick) {
        this.mDoesShowOnClick = showsOnclick;
        return this;
    }

    /**
     * Unregister the view so that it can no longer be long pressed to show the QuickActionView
     *
     * @param view the view to unregister
     */
    public void unregister(View view) {
        mRegisteredListeners.remove(view);
        view.setOnTouchListener(null);
        view.setOnLongClickListener(null);
    }

    /**
     * Adds an action to the QuickActionView
     *
     * @param action the action to add
     * @return the QuickActionView
     */
    public QuickActionView addAction(Action action) {
        checkShown();
        mActions.add(action);
        return this;
    }

    /**
     * Adds a collection of actions to the QuickActionView
     *
     * @param actions the actions to add
     * @return the QuickActionView
     */
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

    /**
     * Removes all actions from the QuickActionView
     *
     * @return the QuickActionView
     */
    public QuickActionView removeActions() {
        mActions.clear();
        return this;
    }

    /**
     * Remove an individual action from the QuickActionView
     *
     * @param actionId the action id
     * @return the QuickActionView
     */
    public QuickActionView removeAction(int actionId) {
        for (int i = 0; i < mActions.size(); i++) {
            if (mActions.get(i).getId() == actionId) {
                mActions.remove(i);
                return this;
            }
        }
        throw new IllegalArgumentException("No action exists for actionId" + actionId);
    }

    /**
     * @param onActionSelectedListener the listener
     * @return the QuickActionView
     * @see OnActionSelectedListener
     */
    public QuickActionView setOnActionSelectedListener(OnActionSelectedListener onActionSelectedListener) {
        mOnActionSelectedListener = onActionSelectedListener;
        return this;
    }

    /**
     * @param onDismissListener the listener
     * @return the QuickActionView
     * @see OnDismissListener
     */
    public QuickActionView setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    /**
     * @param onShowListener the listener
     * @return the QuickActionView
     * @see OnShowListener
     */
    public QuickActionView setOnShowListener(OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
        return this;
    }

    /**
     * @param listener the listener
     * @return the QuickActionView
     * @see OnActionHoverChangedListener
     */
    public QuickActionView setOnActionHoverChangedListener(OnActionHoverChangedListener listener) {
        mOnActionHoverChangedListener = listener;
        return this;
    }

    /**
     * Set the indicator drawable (the drawable that appears at the point the user has long pressed
     *
     * @param indicatorDrawable the indicator drawable
     * @return the QuickActionView
     */
    public QuickActionView setIndicatorDrawable(Drawable indicatorDrawable) {
        mIndicatorDrawable = indicatorDrawable;
        return this;
    }

    /**
     * Set the scrim color (the background behind the QuickActionView)
     *
     * @param scrimColor the desired scrim color
     * @return the QuickActionView
     */
    public QuickActionView setScrimColor(@ColorInt int scrimColor) {
        mScrimColor = scrimColor;
        return this;
    }

    /**
     * Set the drawable that appears behind the Action text labels
     *
     * @param textBackgroundDrawable the desired drawable
     * @return the QuickActionView
     */
    public QuickActionView setTextBackgroundDrawable(@DrawableRes int textBackgroundDrawable) {
        mConfig.setTextBackgroundDrawable(textBackgroundDrawable);
        return this;
    }


    /**
     * Set the background color state list for all action items
     *
     * @param backgroundColorStateList the desired colorstatelist
     * @return the QuickActionView
     */
    public QuickActionView setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
        mConfig.setBackgroundColorStateList(backgroundColorStateList);
        return this;
    }

    /**
     * Set the text color for the Action labels
     *
     * @param textColor the desired text color
     * @return the QuickActionView
     */
    public QuickActionView setTextColor(@ColorInt int textColor) {
        mConfig.setTextColor(textColor);
        return this;
    }

    /**
     * Set the action's background color. If you want to have a pressed state,
     * see {@link #setBackgroundColorStateList(ColorStateList)}
     *
     * @param backgroundColor the desired background color
     * @return the QuickActionView
     */
    public QuickActionView setBackgroundColor(@ColorInt int backgroundColor) {
        mConfig.setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * Set the typeface for the Action labels
     *
     * @param typeface the desired typeface
     * @return the QuickActionView
     */
    public QuickActionView setTypeface(Typeface typeface) {
        mConfig.setTypeface(typeface);
        return this;
    }

    /**
     * Set the text size for the Action labels
     *
     * @param textSize the desired textSize (in pixels)
     * @return the QuickActionView
     */
    public QuickActionView setTextSize(int textSize) {
        mConfig.setTextSize(textSize);
        return this;
    }

    /**
     * Set the text top padding for the Action labels
     *
     * @param textPaddingTop the top padding in pixels
     * @return the QuickActionView
     */
    public QuickActionView setTextPaddingTop(int textPaddingTop) {
        mConfig.setTextPaddingTop(textPaddingTop);
        return this;
    }

    /**
     * Set the text bottom padding for the Action labels
     *
     * @param textPaddingBottom the top padding in pixels
     * @return the QuickActionView
     */
    public QuickActionView setTextPaddingBottom(int textPaddingBottom) {
        mConfig.setTextPaddingBottom(textPaddingBottom);
        return this;
    }

    /**
     * Set the text left padding for the Action labels
     *
     * @param textPaddingLeft the top padding in pixels
     * @return the QuickActionView
     */
    public QuickActionView setTextPaddingLeft(int textPaddingLeft) {
        mConfig.setTextPaddingLeft(textPaddingLeft);
        return this;
    }

    /**
     * Set the text right padding for the Action labels
     *
     * @param textPaddingRight the top padding in pixels
     * @return the QuickActionView
     */
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
     * Override the animations for when the QuickActionView action title shows
     *
     * @param actionsTitleInAnimator the custom animator
     * @return this QuickActionView
     */
    public QuickActionView setActionsTitleInAnimator(ActionsTitleInAnimator actionsTitleInAnimator) {
        mActionsTitleInAnimator = actionsTitleInAnimator;
        return this;
    }

    /**
     * Override the animations for when the QuickActionView dismisses
     *
     * @param actionsTitleOutAnimator the custom animator
     * @return this QuickActionView
     */
    public QuickActionView setActionsTitleOutAnimator(ActionsTitleOutAnimator actionsTitleOutAnimator) {
        mActionsTitleOutAnimator = actionsTitleOutAnimator;
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

    /**
     * Get the center point of the {@link QuickActionView} aka the point at which the actions will eminate from
     *
     * @return the center point, or null if the view has not yet been created
     */
    @Nullable
    public Point getCenterPoint() {
        if (mQuickActionViewLayout != null) {
            return mQuickActionViewLayout.mCenterPoint;
        }
        return null;
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
            if (checkAttachedToWindow(mQuickActionViewLayout)) {
                manager.removeView(mQuickActionViewLayout);
            }
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

    private boolean checkAttachedToWindow(View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            return view.isAttachedToWindow();
        }
        //Unfortunately, we have no way of truly knowing on versions less than 19
        return true;
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

    /**
     * Get the extras associated with the QuickActionView. Allows for
     * saving state to the QuickActionView
     *
     * @return the bundle for the QuickActionView
     */
    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * Set extras to associate with the QuickActionView to allow saving state
     *
     * @param extras the bundle
     * @return the QuickActionView
     */
    public QuickActionView setExtras(Bundle extras) {
        mExtras = extras;
        return this;
    }

    /**
     * Retrieve the view that has been long pressed
     *
     * @return the registered view that was long pressed to show the QuickActionView
     */
    @Nullable
    public View getLongPressedView() {
        return mClickedView;
    }

    /**
     * Listener for when an action is selected (hovered, then released)
     */
    public interface OnActionSelectedListener {
        void onActionSelected(Action action, QuickActionView quickActionView);
    }

    /**
     * Listener for when an action has its hover state changed (hovering or stopped hovering)
     */
    public interface OnActionHoverChangedListener {
        void onActionHoverChanged(Action action, QuickActionView quickActionView, boolean hovering);
    }

    /**
     * Listen for when the QuickActionView is dismissed
     */
    public interface OnDismissListener {
        void onDismiss(QuickActionView quickActionView);
    }

    /**
     * Listener for when the QuickActionView is shown
     */
    public interface OnShowListener {
        void onShow(QuickActionView quickActionView);
    }

    protected static class Config {
        private Action.Config mDefaultConfig;
        private Typeface mTypeface;
        private int mTextSize;
        private int mTextPaddingTop;
        private int mTextPaddingBottom;
        private int mTextPaddingLeft;
        private int mTextPaddingRight;

        protected Config(Context context) {
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

    /**
     * Parent layout that actually houses all of the quick action views
     */
    private class QuickActionViewLayout extends FrameLayout {

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
                if (!TextUtils.isEmpty(action.getTitle())) {
                    ActionTitleView actionTitleView = new ActionTitleView(context, action, helper);
                    actionTitleView.setVisibility(View.GONE);
                    mActionTitleViews.put(action, actionTitleView);
                    FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    addView(actionTitleView, titleParams);
                }
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

                float startAngle = getOptimalStartAngle(entry.getValue().getActionCircleRadiusExpanded());
                ActionView actionView = entry.getValue();
                PointF point = getActionPoint(index, startAngle, actionView);
                point.offset(-actionView.getCircleCenterX(), -actionView.getCircleCenterY());
                actionView.layout((int) point.x, (int) point.y, (int) (point.x + actionView.getMeasuredWidth()), (int) (point.y + actionView.getMeasuredHeight()));
                ActionTitleView titleView = mActionTitleViews.get(entry.getKey());
                if (titleView != null) {
                    float titleLeft = point.x + (actionView.getMeasuredWidth() / 2) - (titleView.getMeasuredWidth() / 2);
                    float titleTop = point.y - 10 - titleView.getMeasuredHeight();
                    titleView.layout((int) titleLeft, (int) titleTop, (int) (titleLeft + titleView.getMeasuredWidth()), (int) (titleTop + titleView.getMeasuredHeight()));
                }
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
            maxDuration = Math.max(maxDuration, animateLabelsOut());
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

        private int animateLabelsOut() {
            for (ActionTitleView view : mActionTitleViews.values()) {
                view.animate().alpha(0).setDuration(100);
            }
            return 200;
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
                            if (insideCircle(getActionPoint(index, getOptimalStartAngle(actionView.getActionCircleRadiusExpanded()), actionView), actionView.getActionCircleRadiusExpanded(), event.getRawX(), event.getRawY())) {
                                if (!actionView.isSelected()) {
                                    actionView.setSelected(true);
                                    actionView.animateInterpolation(1);
                                    ActionTitleView actionTitleView = mActionTitleViews.get(actionView.getAction());
                                    if (actionTitleView != null) {
                                        actionTitleView.setVisibility(View.VISIBLE);
                                        actionTitleView.bringToFront();
                                        mActionsTitleInAnimator.animateActionTitleIn(actionView.getAction(), index, actionTitleView);
                                    }
                                    if (mOnActionHoverChangedListener != null) {
                                        mOnActionHoverChangedListener.onActionHoverChanged(actionView.getAction(), QuickActionView.this, true);
                                    }
                                }
                            } else {
                                if (actionView.isSelected()) {
                                    actionView.setSelected(false);
                                    actionView.animateInterpolation(0);
                                    final ActionTitleView actionTitleView = mActionTitleViews.get(actionView.getAction());
                                    if (actionTitleView != null) {
                                        int timeTaken = mActionsTitleOutAnimator.animateActionTitleOut(actionView.getAction(), index, actionTitleView);
                                        actionTitleView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                actionTitleView.setVisibility(View.GONE);
                                            }
                                        }, timeTaken);
                                    }
                                    if (mOnActionHoverChangedListener != null) {
                                        mOnActionHoverChangedListener.onActionHoverChanged(actionView.getAction(), QuickActionView.this, false);
                                    }
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


        private PointF getActionPoint(int index, float startAngle, ActionView view) {
            PointF point = new PointF(mCenterPoint);
            float angle = (float) (Math.toRadians(startAngle) + getActionOffsetAngle(index, view));
            point.offset((int) (Math.cos(angle) * getTotalRadius(view.getActionCircleRadiusExpanded())), (int) (Math.sin(angle) * getTotalRadius(view.getActionCircleRadiusExpanded())));
            return point;
        }

        private float getActionOffsetAngle(int index, ActionView view) {
            return (float) (index * (2 * (Math.atan2(view.getActionCircleRadiusExpanded() + mActionPadding, getTotalRadius(view.getActionCircleRadiusExpanded())))));
        }

        private float getMaxActionAngle() {
            int index = 0;
            float max = 0;
            for (ActionView actionView : mActionViews.values()) {
                max = getActionOffsetAngle(index, actionView);
                index++;
            }
            return max;
        }

        private float getTotalRadius(float actionViewRadiusExpanded) {
            return mActionDistance + Math.max(mIndicatorView.getWidth(), mIndicatorView.getHeight()) + actionViewRadiusExpanded;
        }

        private float getOptimalStartAngle(float actionViewRadiusExpanded) {
            if (getMeasuredWidth() > 0) {
                float radius = getTotalRadius(actionViewRadiusExpanded);

                int top = -mCenterPoint.y;
                boolean topIntersect = !Double.isNaN(Math.acos(top / radius));

                float horizontalOffset = (mCenterPoint.x - (getMeasuredWidth() / 2.0f)) / (getMeasuredWidth() / 2.0f);

                float angle;
                float offset = (float) Math.pow(Math.abs(horizontalOffset), 1.2) * Math.signum(horizontalOffset);
                if (topIntersect) {
                    angle = 90 + (90 * offset);
                } else {
                    angle = 270 - (90 * offset);
                }
                normalizeAngle(angle);

                return (float) (angle - Math.toDegrees(getMiddleAngleOffset()));
            }
            return (float) (270 - Math.toDegrees(getMiddleAngleOffset()));
        }


        public float normalizeAngle(double angleDegrees) {
            angleDegrees = angleDegrees % (360);
            angleDegrees = (angleDegrees + 360) % 360;
            return (float) angleDegrees;
        }

        private float getMiddleAngleOffset() {
            return getMaxActionAngle() / 2f;
        }

        private boolean insideCircle(PointF center, float radius, float x, float y) {
            return distance(center, x, y) < radius;
        }

        private float distance(PointF point, float x, float y) {
            return (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        }
    }

    /**
     * A class to combine a long click listener and a touch listener, to register views with
     */
    private class RegisteredListener implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

        private float mTouchX;
        private float mTouchY;

        @Override
        public void onClick(View v) {
            show(v, new Point((int) mTouchX, (int) mTouchY));
        }

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
