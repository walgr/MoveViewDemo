package com.wpf.moveview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by 王朋飞 on 2018/1/31.
 *
 */

public class PullableRecyclerView extends RecyclerView implements Pullable {

    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isTop() {
        return isVertical()?
                !canScrollVertically(-1):
                !canScrollHorizontally(-1);
    }

    @Override
    public boolean isBottom() {
        return isVertical()?
                !canScrollVertically(1):
                !canScrollHorizontally(1);
    }

    private boolean isVertical() {
        LayoutManager layoutManager = getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL;
        }
        return true;
    }

    private boolean isHorizontal() {
        LayoutManager layoutManager = getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL;
        }
        return false;
    }
}
