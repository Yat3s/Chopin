package com.yat3s.chopin.sample;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Yat3s on 8/31/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class TitleHeaderDecoration extends RecyclerView.ItemDecoration {
    private static final int DRAW_POSITION = 3;

    private Rect mBounds = new Rect();

    private View mDividerTitleHeaderView;

    public TitleHeaderDecoration(View dividerTitleHeaderView) {
        mDividerTitleHeaderView = dividerTitleHeaderView;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (0 == mDividerTitleHeaderView.getHeight()) {
            measureView(mDividerTitleHeaderView, parent.getWidth(), 0);
        }

        final int childCount = parent.getChildCount();

        for (int idx = 0; idx < childCount; idx++) {
            View itemView = parent.getChildAt(idx);
            parent.getDecoratedBoundsWithMargins(itemView, mBounds);
            final int top = mBounds.top;
            final int left = mBounds.left;
            final int position = parent.getChildAdapterPosition(itemView);
            if (position == DRAW_POSITION) {
                canvas.save();
                canvas.translate(left, top);
                mDividerTitleHeaderView.draw(canvas);
                canvas.restore();
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        if (position / DRAW_POSITION == 1) {
            if (0 == mDividerTitleHeaderView.getHeight()) {
                measureView(mDividerTitleHeaderView, parent.getWidth(), 0);
            }
            outRect.set(0, mDividerTitleHeaderView.getHeight(), 0, 0);
        }
    }

    private void measureView(View view, int width, int height) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

}
