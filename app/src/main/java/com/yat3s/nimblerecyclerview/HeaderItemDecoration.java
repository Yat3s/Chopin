package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


/**
 * Created by Yat3s on 24/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class HeaderItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "HeaderItemDecoration";
    private Paint mPaint;
    private View mView;
    Rect rect = new Rect();

    public HeaderItemDecoration(Context context, View view) {
        mPaint = new Paint();
        mView = view;
        mPaint.setColor(Color.BLUE);
        rect.set(0, 0, 800, 100);
        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY);
        mView.measure(widthSpec, heightSpec);

        Log.d(TAG, "HeaderItemDecoration: " + widthSpec + "," + heightSpec);
        //Lay the view out at the rect width and height
        mView.layout(0, 0, rect.width(), rect.height());
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);

            if (null == itemView) {
                Log.d(TAG, "onDrawOver: item view is null-->" + position);
            }
            canvas.save();
            canvas.translate(rect.left, rect.top);
            mView.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Log.d(TAG, "getItemOffsets: ");
        super.getItemOffsets(outRect, view, parent, state);
    }
}
