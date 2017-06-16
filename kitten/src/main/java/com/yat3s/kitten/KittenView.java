package com.yat3s.kitten;

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

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class KittenView extends ViewGroup {
    private static final String TAG = "NimbleRecyclerView";
    private static final int mVisibleThreshold = 4;
    private static final int SCROLLER_DURATION = 800;
    private static final float SCROLL_RESISTANCE = 0.64f;

    private boolean isLoadingMore = false;
    private RecyclerView mRecyclerView;
    private int mLastY;
    private Scroller mScroller;
    private View mRefreshHeaderView;
    private RefreshHeaderViewProvider mRefreshHeaderViewProvider;
    private boolean isRefreshing;

    // Listener
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    public KittenView(Context context) {
        this(context, null);
    }

    public KittenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KittenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void setRefreshHeaderView(RefreshHeaderViewProvider refreshHeaderViewProvider) {
        mRefreshHeaderViewProvider = refreshHeaderViewProvider;
        mRefreshHeaderView = refreshHeaderViewProvider.provideContentView();
        if (null != mRefreshHeaderView) {
            mRefreshHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mRefreshHeaderView);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
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
        if (null != mRefreshHeaderView) {
            mRefreshHeaderView.layout(0, -mRefreshHeaderView.getMeasuredHeight(), mRefreshHeaderView.getMeasuredWidth(), 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mLastTouchY = y;
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

    int mLastTouchY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = y - mLastTouchY;

                Log.d(TAG, "mLastTouchY: " + mLastTouchY);
                Log.d(TAG, "offsetY: " + offsetY);

                scrollBy(0, (int) (-offsetY * SCROLL_RESISTANCE));
                mLastTouchY = y;

                if (null != mRefreshHeaderViewProvider) {
                    int progress = -getScrollY() > mRefreshHeaderView.getMeasuredHeight() ? 100 : -100 * getScrollY() /
                            mRefreshHeaderView.getMeasuredHeight();
                    mRefreshHeaderViewProvider.onRefreshHeaderViewScrollChange(progress);
                }

                return true;
            case MotionEvent.ACTION_UP:
                // Ignore any when refreshing.
                if (isRefreshing) {
                    return true;
                }
                if (-getScrollY() >= mRefreshHeaderView.getMeasuredHeight()) {
                    releaseToStartRefresh();
                } else {
                    // Ignore this refresh.
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void releaseToStartRefresh() {
        mScroller.startScroll(0, getScrollY(), 0, -(mRefreshHeaderView.getMeasuredHeight() + getScrollY()), SCROLLER_DURATION);
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderViewProvider) {
            mRefreshHeaderViewProvider.onStartRefresh();
        }
    }

    public void refreshComplete() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        isRefreshing = false;
        if (null != mRefreshHeaderViewProvider) {
            mRefreshHeaderViewProvider.onRefreshComplete();
        }
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

    public interface RefreshHeaderViewProvider {
        View provideContentView();

        void onStartRefresh();

        void onRefreshComplete();

        void onRefreshHeaderViewScrollChange(int progress);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
