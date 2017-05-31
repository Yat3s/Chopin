package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.security.SecureRandom;


/**
 * Created by Yat3s on 24/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class HeaderItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "HeaderItemDecoration";
    private Paint mPaint;
    private View mView;
    Rect mBounds = new Rect();

    public HeaderItemDecoration(Context context, View view) {
        mPaint = new Paint();
        mView = view;
        mPaint.setColor(Color.BLUE);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }
        if (mView.getWidth() <= 0) {
            measureHeader(parent.getWidth());
        }

        int left = 0;
        for (int idx = 1; idx < childCount; idx++) {
            View itemView = parent.getChildAt(idx);
            parent.getDecoratedBoundsWithMargins(itemView, mBounds);
            final int top = mBounds.top;
            final int position = parent.getChildAdapterPosition(itemView);
            if (hasHeader(position)) {
                drawHeader(position, left, top, canvas);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mView.getWidth() <= 0) {
            measureHeader(parent.getWidth());
        }

        View nearestHasHeaderChildView = null;
        int childCount = parent.getChildCount();

        // Find nearest child view with header.
        for (int idx = 1; idx < childCount; idx++) {
            int position = parent.getChildAdapterPosition(parent.getChildAt(idx));
            if (hasHeader(position)) {
                nearestHasHeaderChildView = parent.getChildAt(idx);
                break;
            }
        }

        if (null == nearestHasHeaderChildView) {
            drawHeader(0, 0, 0, canvas);
            return;
        }

        int position = parent.getChildAdapterPosition(nearestHasHeaderChildView);
        int top = (int) (nearestHasHeaderChildView.getY() - 2 * mView.getHeight());
        Log.d(TAG, "onDrawOver: top" + top);
        Log.d(TAG, "onDrawOver: firstChild.getY()" + nearestHasHeaderChildView.getY());
        if (hasHeader(position) && top < 0) {
            drawHeader(position, 0, top, canvas);
        } else {
            drawHeader(position, 0, 0, canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mView.getWidth() <= 0) {
            measureHeader(parent.getWidth());
        }
        final int position = parent.getChildAdapterPosition(view);
        if (hasHeader(position)) {
            outRect.set(0, mView.getHeight(), 0, 0);
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }

    private void drawHeader(int position, int left, int top, Canvas canvas) {
        mView.setBackgroundColor(generateColor(position));
        canvas.save();
        canvas.translate(left, top);
        mView.draw(canvas);
        canvas.restore();
    }

    private void measureHeader(int width) {
        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mView.measure(widthSpec, heightSpec);

        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
    }

    private int generateColor(int position) {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                position, 1, 1
        });
    }

    private boolean hasHeader(int position) {
        return position % 5 == 0 && position != 0;
    }
}
