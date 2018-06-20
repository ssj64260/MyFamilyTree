package com.cxb.myfamilytree.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cxb.myfamilytree.utils.DisplayUtil;


/**
 * RecyclerView 分割线
 */

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

    private int mDividerHeight;
    private int mColor;
    private Paint mDividerPaint;

    public RecyclerViewDecoration(Context context) {
        this(context, 10);
    }

    public RecyclerViewDecoration(Context context, int heightDP) {
        this(context, heightDP, 0);
    }

    public RecyclerViewDecoration(Context context, int heightDP, @ColorRes int colorId) {
        mDividerHeight = DisplayUtil.dip2px(heightDP);
        if (colorId > 0) {
            mColor = ContextCompat.getColor(context, colorId);
        } else {
            mColor = colorId;
        }

        mDividerPaint = new Paint();
        mDividerPaint.reset();
        mDividerPaint.setColor(mColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + mDividerHeight;
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mDividerHeight;
    }
}
