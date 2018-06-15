package com.wpf.moveviewdemo;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 王朋飞 on 2017/10/20.
 *
 * @Title
 * @Package com.wpf.moveviewdemo
 * @Description: ${TODO} (用一句话描述该文件做什么)
 * @LastModifiedTime 2017/10/20
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private int orientation = LinearLayoutManager.VERTICAL;
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams((orientation == LinearLayoutManager.VERTICAL) ?
                new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,56*3):
                new RecyclerView.LayoutParams(56*3,ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(24);
        return new SimpleViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
