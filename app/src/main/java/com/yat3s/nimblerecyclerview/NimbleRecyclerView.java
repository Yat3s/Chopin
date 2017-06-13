package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class NimbleRecyclerView extends ViewGroup {
    private static final String TAG = "NimbleRecyclerView";
    private static final int mVisibleThreshold = 4;

    private boolean isLoadingMore = false;
    private RecyclerView mRecyclerView;
    private int mLastX, mLastY;
    private Scroller mScroller;

    // Listener
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    public NimbleRecyclerView(Context context) {
        this(context, null);
    }

    public NimbleRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NimbleRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
        addView(mRecyclerView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int idx = 0; idx < getChildCount(); idx++) {
            measureChild(getChildAt(idx), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mRecyclerView.layout(0, 0, mRecyclerView.getMeasuredWidth(), mRecyclerView.getMeasuredHeight());
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX(), y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll down
                if (y - mLastY > 0) {
                    return recyclerViewScrolledToTop();
                }
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX(), y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN: " + x + ", " + y);
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: " + x + ", " + y);
                int offsetX = x - mLastX;
                int offsetY = y - mLastY;
                scrollTo(0, -offsetY);
                return true;
            case MotionEvent.ACTION_UP:
                releaseRefresh();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void releaseRefresh() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
    }


    private boolean recyclerViewScrolledToTop() {
        return mRecyclerView.computeVerticalScrollOffset() <= 0;
    }

    private void initialize() {
        mScroller = new Scroller(getContext());
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
        }
        postInvalidate();
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void loadMoreComplete() {
        isLoadingMore = false;
    }

    public void refreshComplete() {
//        mPtrFrameLayout.refreshComplete();
    }

    public void setRefreshHeaderView(RefreshHeaderView headerView) {
//
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // if dy>0 --> pull down
                if (lastVisibleItem >= totalItemCount - mVisibleThreshold && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }


    public static class RefreshHeaderViewBuilder {
        private RefreshHeaderView mRefreshView;
        private View mContentView;

        public RefreshHeaderViewBuilder(Context context, View refreshView) {
            mRefreshView = new RefreshHeaderView(context);
            mContentView = refreshView;
            mRefreshView.addView(mContentView);
        }

        public RefreshHeaderViewBuilder setHeaderViewRefreshListener(HeaderViewRefreshListener listener) {
            mRefreshView.setHeaderViewRefreshListener(listener);
            return this;
        }

        public RefreshHeaderView build() {
            return mRefreshView;
        }

    }

    private static class RefreshHeaderView extends ViewGroup implements PtrUIHandler {
        private HeaderViewRefreshListener mListener;

        public RefreshHeaderView(Context context) {
            this(context, null);
        }

        public RefreshHeaderView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RefreshHeaderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void setHeaderViewRefreshListener(HeaderViewRefreshListener listener) {
            mListener = listener;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        @Override
        public void onUIReset(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {
            if (null != mListener) {
                mListener.onStartRefresh();
            }
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {

        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        }
    }

    public interface HeaderViewRefreshListener {
        void onStartRefresh();

    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
