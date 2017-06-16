package com.yat3s.kitten.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import com.yat3s.kitten.adapter.NimbleViewHolder;
import com.yat3s.kitten.adapter.StickyHeaderAdapter;


/**
 * Created by Yat3s on 24/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "StickyHeaderItemDecoration";
    private Paint mPaint;
    Rect mBounds = new Rect();
    private StickyHeaderAdapter mStickyHeaderAdapter;
    private SparseArray<View> mHeaderViewCache;
    private Context mContext;
    private int mCurrentStickyHeaderHeight;

    public StickyHeaderItemDecoration(Context context, StickyHeaderAdapter adapter) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mStickyHeaderAdapter = adapter;
        mHeaderViewCache = new SparseArray<>();
        mContext = context;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }

        int left = 0;
        for (int idx = 1; idx < childCount; idx++) {
            View itemView = parent.getChildAt(idx);
            parent.getDecoratedBoundsWithMargins(itemView, mBounds);
            final int top = mBounds.top;
            final int position = parent.getChildAdapterPosition(itemView);
            if (hasHeader(position)) {
                drawHeader(parent, position, left, top, canvas);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

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
            drawHeaderOver(parent, 0, 0, 0, canvas);
            return;
        }

        int position = parent.getChildAdapterPosition(nearestHasHeaderChildView);
        int top = (int) (nearestHasHeaderChildView.getY() - 2 * mCurrentStickyHeaderHeight);
        if (hasHeader(position) && top < 0) {
            drawHeaderOver(parent, position, 0, top, canvas);
        } else {
            drawHeaderOver(parent, position, 0, 0, canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        if (hasHeader(position)) {
            View headerView = getHeaderViewAndBindViewHolder(position, parent);
            if (headerView.getHeight() <= 0) {
                measureView(headerView, parent.getWidth(), 0);
            }
            outRect.set(0, headerView.getHeight(), 0, 0);
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }

    private int lastMeasureOverItemPosition = -1;

    private void drawHeaderOver(RecyclerView parent, int position, int left, int top, Canvas canvas) {
        View headerView = getHeaderViewAndBindViewHolder(position, parent);


        // Measure & layout
        if (lastMeasureOverItemPosition != position) {
            measureView(headerView, parent.getWidth(), 0);
            mCurrentStickyHeaderHeight = headerView.getMeasuredHeight();
            lastMeasureOverItemPosition = position;
            Log.d(TAG, "MeasureItemViewOver: " + position);
        }

        drawView(canvas, headerView, left, top);
    }

    private int lastMeasureItemPosition = -1;

    private void drawHeader(RecyclerView parent, int position, int left, int top, Canvas canvas) {
        View headerView = getHeaderViewAndBindViewHolder(position, parent);

        // Measure & layout
        if (lastMeasureItemPosition != position) {
            measureView(headerView, parent.getWidth(), 0);
            Log.d(TAG, "MeasureItemView: " + position);
            lastMeasureItemPosition = position;
        }
        drawView(canvas, headerView, left, top);
    }

    private boolean hasHeader(int position) {
        return mStickyHeaderAdapter.hasHeader(position);
    }

    private void drawView(Canvas canvas, View needDrawView, int left, int top) {
        canvas.save();
        canvas.translate(left, top);
        needDrawView.draw(canvas);
        canvas.restore();
    }

    private void measureView(View view, int width, int height) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private View getHeaderViewAndBindViewHolder(int position, RecyclerView parent) {
        int layoutResId = mStickyHeaderAdapter.getHeaderViewLayoutId(position);
        View headerView = mHeaderViewCache.get(layoutResId);
        if (null == headerView) {
            View inflatedView = LayoutInflater
                    .from(mContext)
                    .inflate(layoutResId, parent, false);

            RecyclerView.ViewHolder vh = new NimbleViewHolder(inflatedView);
            mStickyHeaderAdapter.onBindHeaderViewHolder(vh, position);
            headerView = vh.itemView;

            measureView(headerView, parent.getWidth(), 0);
            mHeaderViewCache.put(layoutResId, headerView);
        }

        return headerView;
    }
}
