package com.ovenbits.quickactionview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by Alex on 12/7/15.
 */
public class QAV {

    private boolean mShown = false;
    private Context mContext;
    private onActionSelectedListener mOnActionSelectedListener;
    private onDismissListener mOnDismissListener;
    private onShowListener mOnShowListener;


    private Float mStartAngle;
    private float mActionDistance;
    private int mActionPadding;


    private ArrayList<Action> mActions = new ArrayList<>();
    private Bundle mExtras;
    private QuickActionViewLayout mQuickActionViewLayout;

    private View mRegisteredView;
    private View.OnLongClickListener mOnLongClickListener;
    private Config mConfig;
    private Drawable mIndicatorDrawable;

    private QAV(Context context) {
        mContext = context;
    }

    public static QAV with(Context context) {
        return new QAV(context);
    }

    public QAV show(View anchor, Point offset) {
        if (mShown) {
            throw new RuntimeException("Show cannot be called when the QuickActionView is already visible");
        }
        int[] loc = new int[2];
        anchor.getLocationInWindow(loc);

        Point point = new Point(offset);
        point.offset(loc[0], loc[1]);
        return show(point);
    }

    public QAV show(Point point) {
        if (mShown) {
            throw new RuntimeException("Show cannot be called when the QuickActionView is already visible");
        }
        mShown = true;
        return this;
    }

    public QAV register(View view) {
        mRegisteredView = view;
        mRegisteredView = view;
        mRegisteredView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mTouchX = event.getX();
//                mTouchY = event.getY();
//
//                if (getVisibility() == View.VISIBLE) {
//                    return onTouchEvent(event);
//                }
                return false;
            }
        });
        mRegisteredView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show();
                return false;
            }
        });
        return this;
    }

    public QAV addAction(Action action) {
        checkShown();
        mActions.add(action);
        return this;
    }

    public QAV setActions(@MenuRes int menuId) {
        Menu menu = new MenuBuilder(mContext);
        new MenuInflater(mContext).inflate(menuId, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Action action = new Action(item.getItemId(), item.getIcon(), item.getTitle());
            addAction(action);
        }
        return this;
    }


    public QAV addActions(Collection<Action> actions) {
        checkShown();
        mActions.addAll(actions);
        return this;
    }

    public QAV setOnActionSelectedListener(onActionSelectedListener onActionSelectedListener) {
        mOnActionSelectedListener = onActionSelectedListener;
        return this;
    }

    public QAV setOnDismissListener(onDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public QAV setOnShowListener(onShowListener onShowListener) {
        mOnShowListener = onShowListener;
        return this;
    }

    public void setStartAngle(Float startAngle) {
        checkShown();
        mStartAngle = startAngle;
    }

    public void setIndicatorDrawable(Drawable indicatorDrawable) {
        mIndicatorDrawable = indicatorDrawable;
    }

    private void display(Point point) {
        if (mActions == null) {
            throw new IllegalStateException("You need to give the QuickActionView actions before calling show!");
        }
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        params.x = point.x;
        params.y = point.y;
//        mInnerCirclePoint = point;
//        setPivotX(point.x);
//        setPivotY(point.y);
//        setScaleX(0);
//        setScaleY(0);
//        setAlpha(0);
//        setVisibility(View.VISIBLE);
//        setEnabled(true);
//        animate().scaleX(1).scaleY(1).alpha(1).setDuration(DURATION_ANIMATION).setInterpolator(new OvershootInterpolator()).start();
//        float angle = (float) Math.toDegrees(Math.atan2(0 - point.y, 0 - point.x));
//        if (angle < 0) {
//            angle += 360;
//        }
//        setStartAngle(angle);
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.format = PixelFormat.TRANSLUCENT;
//        mFrameLayoutHolder = new FrameLayout(getContext());
//        mFrameLayoutHolder.setBackgroundColor(mScrimColor);
//        manager.addView(mFrameLayoutHolder, params);
//        FrameLayout.LayoutParams holderParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mFrameLayoutHolder.addView(this, holderParams);
//        mFrameLayoutHolder.setAlpha(0);
//        mFrameLayoutHolder.animate().alpha(1).setDuration(DURATION_ANIMATION).start();
//        if (mListener != null) {
//            mListener.onQuickActionShow();
//        }
    }

    private void checkShown() {
        if (mShown) {
            throw new RuntimeException("QuickActionView cannot be configured if show has already been called.");
        }
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public QAV setExtras(Bundle extras) {
        mExtras = extras;
        return this;
    }


    public interface onActionSelectedListener {
        public void onActionSelected(Action action, QAV qav);
    }

    public interface onDismissListener {
        public void onDismiss(QAV qav);
    }

    public interface onShowListener {
        public void onShow(QAV qav);
    }

    public static class Config {
        private Action.Config mDefaultConfig;
        private Typeface mTypeface;
        private int mTextSize;

        public Config(Context context) {
            this(context, null, context.getResources().getDimensionPixelSize(R.dimen.qav_action_title_view_text_size));
        }

        public Config(Context context, Typeface typeface, int textSize) {
            mTypeface = typeface;
            mTextSize = textSize;
            mDefaultConfig = new Action.Config(context);
        }

        public ColorStateList getIconColorStateList() {
            return mDefaultConfig.getIconColorStateList();
        }

        public void setIconColorStateList(ColorStateList iconColorStateList) {
            mDefaultConfig.setIconColorStateList(iconColorStateList);
        }

        public Drawable getTextBackgroundDrawable() {
            return mDefaultConfig.getTextBackgroundDrawable();
        }

        public void setTextBackgroundDrawable(Drawable textBackgroundDrawable) {
            mDefaultConfig.setTextBackgroundDrawable(textBackgroundDrawable);
        }

        public ColorStateList getTextColorStateList() {
            return mDefaultConfig.getTextColorStateList();
        }

        public void setTextColorStateList(ColorStateList textColorStateList) {
            mDefaultConfig.setTextColorStateList(textColorStateList);
        }

        public ColorStateList getBackgroundColorStateList() {
            return mDefaultConfig.getBackgroundColorStateList();
        }

        public void setBackgroundColorStateList(ColorStateList backgroundColorStateList) {
            mDefaultConfig.setBackgroundColorStateList(backgroundColorStateList);
        }

        public void setTextColor(@ColorInt int textColor) {
            mDefaultConfig.setTextColor(textColor);
        }

        public void setBackgroundColor(@ColorInt int backgroundColor) {
            mDefaultConfig.setBackgroundColor(backgroundColor);
        }

        public void setIconColor(@ColorInt int iconColor) {
            mDefaultConfig.setIconColor(iconColor);
        }

        public Typeface getTypeface() {
            return mTypeface;
        }

        public int getTextSize() {
            return mTextSize;
        }
    }

    protected class QuickActionViewLayout extends FrameLayout {

        private Point mCenterPoint;
        private View mIndicatorView;
        private LinkedHashMap<Action, ActionView> mActionViews = new LinkedHashMap<>();
        private LinkedHashMap<Action, ActionTitleView> mActionTitleViews = new LinkedHashMap<>();

        public QuickActionViewLayout(Context context, ArrayList<Action> actions, Point centerPoint) {
            super(context);
            mCenterPoint = centerPoint;
            mIndicatorView = new View(context);
            mIndicatorView.setBackgroundDrawable(mIndicatorDrawable);
            for (Action action : actions) {
                ActionView actionView = new ActionView(context, action);
                mActionViews.put(action, actionView);
                addView(actionView);
                ActionTitleView actionTitleView = new ActionTitleView(context, action);
                mActionTitleViews.put(action, actionTitleView);
                addView(actionTitleView);
            }
        }


        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);

            mIndicatorView.layout(mCenterPoint.x - (int) (mIndicatorView.getMeasuredWidth() / 2.0),
                    mCenterPoint.y - (int) (mIndicatorView.getMeasuredHeight() / 2.0),
                    mCenterPoint.x + (int) (mIndicatorView.getMeasuredWidth() / 2.0),
                    mCenterPoint.y + (int) (mIndicatorView.getMeasuredHeight() / 2.0));
            for (ActionView actionView : mActionViews.values()) {

            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }


//        private Point getActionPoint(int index) {
//            float angle = (float) (Math.toRadians(mStartAngle) + index * 2 * (Math.atan2(mActionCircleRadiusExpanded + mAngularSpacing, mActionDistance)));
//            Point point = new Point(mCenterPoint);
//            point.offset((int) (Math.cos(angle) * (mTouchCircleRadius + mLineDistance)), (int) (Math.sin(angle) * (mTouchCircleRadius + mLineDistance)));
//            return point;
//        }
    }


}
