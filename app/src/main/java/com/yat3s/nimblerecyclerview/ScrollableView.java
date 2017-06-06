package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by Yat3s on 06/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ScrollableView extends FrameLayout {
    private static final String TAG = "ScrollableView";
    private Scroller mScroller;
    private int mTouchSlop;

    public ScrollableView(Context context) {
        this(context, null);
    }

    public ScrollableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();

        Log.d(TAG, "mTouchSlop: " + mTouchSlop);
        initialize();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX(), y = (int) event.getRawY();
        Log.d(TAG, "onTouchEvent: " + event.getAction() + "--->" + x + ", " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                starBackHome(x, y);
                break;
        }
        return true;
    }

    private void initialize() {
        mScroller = new Scroller(getContext());
    }

    private void move(int x, int y) {
        Log.d(TAG, "move: " + x + "， " + y);
        setTranslationX(x);
        setTranslationY(y);
    }

    public void starBackHome(int x, int y) {
        Log.d(TAG, "starBackHome: " + x + ", " + y);
        mScroller.startScroll(x, y, 100, 100, 2000);
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.d(TAG, "computeScroll: " + mScroller.getCurrX() + ", " + mScroller.getCurrY());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
