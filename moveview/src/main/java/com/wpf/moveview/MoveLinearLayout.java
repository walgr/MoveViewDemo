package com.wpf.moveview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by 王朋飞 on 6-14-0014.
 * 模仿IOS的可移动View
 */

public class MoveLinearLayout extends LinearLayout implements
        View.OnTouchListener {

    private ValueAnimator valueAnimator;
    private int delayTime = 500;
    private float y = 0,oldTop;
    private int left,right,height;
    private int reduce = 1;

    public MoveLinearLayout(Context context) {
        this(context,null);
    }

    public MoveLinearLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MoveLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        oldTop = getTop();
        left = getLeft();
        right = getRight();
        height = getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(valueAnimator != null && valueAnimator.isStarted())
                    valueAnimator.cancel();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float top = getTop();
                float offY = event.getY() - y;
                if(offY != 0) moveY(getMove(offY,Math.abs(top)));
                break;
            case MotionEvent.ACTION_UP:
                reBack();
                break;
        }
        return true;
    }

    private float getMove(float offY,float Y) {
        if(Y >= height /10 && reduce < Math.abs(offY) *10) reduce*=2;
        offY/=reduce;
        return offY;
    }

    private void reBack() {
        valueAnimator = ValueAnimator.ofFloat(getTop(),oldTop)
                .setDuration(delayTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offY = ((Float) animation.getAnimatedValue());
                layout(left,(int)offY,right,height+(int)offY);
            }
        });
        valueAnimator.start();
        reduce = 1;
    }

    private void moveY(float offY) {
        layout(left,getTop()+(int) offY,right,getBottom()+(int) offY);
    }
}
