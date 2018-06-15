package com.wpf.moveview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * Created by 王朋飞 on 2018/4/10.
 * 弹性滑动
 */
public class MoveView extends LinearLayout implements
        NestedScrollingParent2 {

    private View mChildView;
    private Pullable mPullable;

    private Scroller mScroller;
    private OverScroller mScroller_Non_Touch;

    private int maxScroll;

    //滑动方向
    private int orientation = LinearLayoutManager.VERTICAL;

    public MoveView(Context context) {
        this(context, null);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MoveView,defStyle,0);
        orientation = a.getInt(R.styleable.MoveView_orientation,-1);
        if(orientation < 0) orientation = LinearLayoutManager.VERTICAL;
        a.recycle();
        mScroller = new Scroller(context, new AccelerateInterpolator());
        mScroller_Non_Touch = new OverScroller(context, new LinearInterpolator());
        maxScroll = 1000;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); ++i) {
            View view = getChildAt(i);
            if (view instanceof NestedScrollingChild) {
                mChildView = view;
                post(new Runnable() {
                    @Override
                    public void run() {
                        maxScroll = mChildView.getHeight()/3;
                    }
                });
                if (mChildView instanceof Pullable)
                    mPullable = (Pullable) mChildView;
                break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ViewCompat.startNestedScroll(mChildView, isVertical() ?
                        ViewCompat.SCROLL_AXIS_VERTICAL :
                        ViewCompat.SCROLL_AXIS_HORIZONTAL);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onStopNestedScroll(mChildView, ViewCompat.TYPE_NON_TOUCH);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes, int type) {
        return (nestedScrollAxes &
                (orientation == LinearLayoutManager.VERTICAL ?
                        ViewCompat.SCROLL_AXIS_VERTICAL :
                        ViewCompat.SCROLL_AXIS_HORIZONTAL)) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes, int type) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        if (!mScroller_Non_Touch.isFinished()) {
            mScroller_Non_Touch.abortAnimation();
            state = 0;
        }
    }

    @Override
    public void onStopNestedScroll(View child, int type) {
        int scrollXY = getScrollXY();
        if (scrollXY != 0 && type == ViewCompat.TYPE_NON_TOUCH) {
            if (mScroller.isFinished()) {
                if (isVertical())
                    mScroller.startScroll(getScrollX(), scrollXY, 0, -scrollXY, 250);
                else
                    mScroller.startScroll(scrollXY, getScrollY(), -scrollXY, 0, 250);
                invalidate();
            }
        }
    }

    private int STATE_NON_TOUCH_SCROLL = 1;

    private int state;

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        //消除上弹过程中触摸漂移
        if (type != ViewCompat.TYPE_NON_TOUCH) return;
        if (isVertical()) {
            if (dyUnconsumed == 0 || dyConsumed == 0) return;
            state = STATE_NON_TOUCH_SCROLL;
            mScroller_Non_Touch.startScroll(0, 0, 0, dyUnconsumed + dyConsumed);
            invalidate();
        } else if (isHorizontal()) {
            if (dxUnconsumed == 0 || dxConsumed == 0) return;
            state = STATE_NON_TOUCH_SCROLL;
            mScroller_Non_Touch.startScroll(0, 0, dxConsumed + dxUnconsumed, 0);
            invalidate();
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //不考虑滑到顶
        if (type == ViewCompat.TYPE_NON_TOUCH) return;
        int scrollXY = getScrollXY();
        int dxy = getDXY(dx, dy);
        if (scrollXY == 0) {
            if (isTop(target) && dxy < 0) {
                scroll(dxy, consumed);
            } else if (isBottom(target) && dxy > 0) {
                scroll(dxy, consumed);
            }
        } else if (scrollXY < 0) {
            if (dxy <= 0) {
                scroll(dxy, consumed, scrollFactor(scrollXY));
            } else {
                if (dxy + scrollXY > 0) dxy = -scrollXY;
                scroll(dxy, consumed);
            }
        } else {
            if (dxy >= 0) {
                scroll(dxy, consumed, scrollFactor(scrollXY));
            } else {
                if (dxy + scrollXY < 0) dxy = -scrollXY;
                scroll(dxy, consumed);
            }
        }
    }

    private void scroll(int dxy, int[] consumed) {
        scroll(dxy, consumed, 1);
    }

    private void scroll(int dxy, int[] consumed, int scrollFactor) {
        if (dxy == 0 || scrollFactor == 0) return;
        if (isVertical()) {
            scrollBy(0, dxy / scrollFactor);
            if (consumed != null) consumed[1] = dxy / scrollFactor;
        } else {
            scrollBy(dxy / scrollFactor, 0);
            if (consumed != null) consumed[0] = dxy / scrollFactor;
        }
    }

    private int scrollFactor() {
        return scrollFactor(getScrollY());
    }

    private int scrollFactor(int scrollXY) {
        scrollXY = Math.abs(scrollXY);
        if (scrollXY > maxScroll * 3)
            return 1 << 8;
        else if (scrollXY > maxScroll * 2.5)
            return 1 << 7;
        else if (scrollXY > maxScroll * 2)
            return 1 << 6;
        else if (scrollXY > maxScroll * 1.5)
            return 1 << 5;
        else if (scrollXY > maxScroll)
            return 1 << 4;
        else if (scrollXY > maxScroll >> 1)
            return 1 << 3;
        else if (scrollXY > maxScroll >> 2)
            return 1 << 2;
        else if (scrollXY > maxScroll >> 3)
            return 1 << 1;

        return 1;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //消除下拉中长时间滑屏的部分
        return getScrollXY() != 0 && !(Math.abs(
                isVertical() ? velocityY : velocityX) <= 500);
    }

    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (isVertical())
                scrollTo(0, mScroller.getCurrY());
            else
                scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
        if (mScroller_Non_Touch.computeScrollOffset()) {
            if (isVertical())
                scrollTo(0, mScroller_Non_Touch.getCurrY());
            else
                scrollTo(mScroller_Non_Touch.getCurrX(), 0);
            invalidate();
        } else {
            if (state == STATE_NON_TOUCH_SCROLL) {
                onStopNestedScroll(mChildView, ViewCompat.TYPE_NON_TOUCH);
                state = 0;
            }
        }
    }

    private boolean isTop(View target) {
        if (mPullable != null) return mPullable.isTop();
        return isVertical() ?
                !target.canScrollVertically(-1) :
                !target.canScrollHorizontally(-1);
    }

    private boolean isBottom(View target) {
        if (mPullable != null) return mPullable.isBottom();
        return isVertical() ?
                !target.canScrollVertically(1) :
                !target.canScrollHorizontally(1);
    }

    private int getScrollXY() {
        if (isVertical()) return getScrollY();
        else if (isHorizontal()) return getScrollX();
        return getScrollY();
    }

    private int getDXY(int dx, int dy) {
        if (isVertical()) return dy;
        else if (isHorizontal()) return dx;
        return dy;
    }

    private boolean isVertical() {
        return orientation == LinearLayoutManager.VERTICAL;
    }

    private boolean isHorizontal() {
        return orientation == LinearLayoutManager.HORIZONTAL;
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new RuntimeException("orientation must VERTICAL and HORIZONTAL");
        }
        this.orientation = orientation;
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    public void setMaxScroll(int maxScroll) {
        this.maxScroll = maxScroll;
    }
}
