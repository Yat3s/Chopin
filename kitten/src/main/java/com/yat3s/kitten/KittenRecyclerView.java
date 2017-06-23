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

import com.yat3s.kitten.decoration.LoadingFooterViewProvider;
import com.yat3s.kitten.decoration.RefreshHeaderViewProvider;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class KittenRecyclerView extends ViewGroup {
    private static final String TAG = "NimbleRecyclerView";
    private static final int mVisibleThreshold = 4;
    private static final int SCROLLER_DURATION = 800;
    private static final float SCROLL_RESISTANCE = 0.64f;
    private static final float SCROLL_FLING_FRICTION = 0.8f;

    private RecyclerView mRecyclerView;

    // The last touch y while intercepted touch event.
    private int mLastTouchY;

    // The Scroller for scroll whole view natural.
    private Scroller mScroller;

    // The Refresh Header View.
    private View mRefreshHeaderView;

    // The Loading Footer View.
    private View mLoadingFooterView;

    // The provider for provide header view and some interfaces for interaction, eg. header view animation.
    private RefreshHeaderViewProvider mRefreshHeaderViewProvider;

    // The provider for provide footer view and some interfaces for interaction, eg. footer view animation.
    private LoadingFooterViewProvider mLoadingFooterViewProvider;

    // Knowing whether recycler view is refreshing.
    private boolean isRefreshing;

    // Knowing whether recycler view is loading more.
    private boolean isLoadingMore;

    // The refresh listener
    private OnRefreshListener mOnRefreshListener;

    // The load more listener
    private OnLoadMoreListener mOnLoadMoreListener;

    public KittenRecyclerView(Context context) {
        this(context, null);
    }

    public KittenRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KittenRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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

    public void setLoadingFooterView(LoadingFooterViewProvider loadingFooterViewProvider) {
        mLoadingFooterViewProvider = loadingFooterViewProvider;
        mLoadingFooterView = loadingFooterViewProvider.provideContentView();
        if (null != mLoadingFooterView) {
            mLoadingFooterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mLoadingFooterView);
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
        // Measure recycler.
        mRecyclerView.layout(0, 0, mRecyclerView.getMeasuredWidth(), mRecyclerView.getMeasuredHeight());

        // Measure refresh header.
        if (null != mRefreshHeaderView) {
            mRefreshHeaderView.layout(0, -mRefreshHeaderView.getMeasuredHeight(), mRefreshHeaderView.getMeasuredWidth(), 0);
        }

        // Measure loading footer.
        if (null != mLoadingFooterView) {
            mLoadingFooterView.layout(0,
                    mRecyclerView.getMeasuredHeight(),
                    mLoadingFooterView.getMeasuredWidth(),
                    mRecyclerView.getMeasuredHeight() + mLoadingFooterView.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Scroll to : " + (y - mLastTouchY > 0 ? "Top" : "Bottom"));

                // Intercept down glide event when scroll to top
                if (y - mLastTouchY > 0) {
                    return recyclerViewScrolledToTop();
                }

                // Intercept up glide event when scroll to bottom
                if (y - mLastTouchY < 0) {
                    return recyclerViewScrolledToBottom();
                }

            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = mLastTouchY - y;

                scrollBy(0, (int) (offsetY * SCROLL_RESISTANCE));
                mLastTouchY = y;

                if (null != mRefreshHeaderViewProvider) {
                    int progress;
                    // Scroll distance has over header view height.
                    if (-getScrollY() > mRefreshHeaderView.getMeasuredHeight()) {
                        progress = 100;
                    } else {
                        progress = 100 * -getScrollY() / mRefreshHeaderView.getMeasuredHeight();
                    }
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
                } else if (getScrollY() >= mLoadingFooterView.getMeasuredHeight()) {
                    releaseToLoadingMore();
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
            mRefreshHeaderViewProvider.onRefreshStart();
        }
    }

    private void releaseToLoadingMore() {
        mScroller.startScroll(0, getScrollY(), 0, (getScrollY() - mLoadingFooterView.getMeasuredHeight()), SCROLLER_DURATION);
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
        if (null != mLoadingFooterViewProvider) {
            mLoadingFooterViewProvider.onLoadingStart();
        }
    }

    public void refreshComplete() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        isRefreshing = false;
        if (null != mRefreshHeaderViewProvider) {
            mRefreshHeaderViewProvider.onRefreshComplete();
        }
    }

    public void loadMoreComplete() {
        isLoadingMore = false;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        if (null != mLoadingFooterViewProvider) {
            mLoadingFooterViewProvider.onLoadingComplete();
        }
    }

    private boolean recyclerViewScrolledToTop() {
        boolean scrollToTop = mRecyclerView.computeVerticalScrollOffset() <= 0;
        if (scrollToTop) {
            Log.d(TAG, "recyclerViewScrolledToTop: ");
        }
        return scrollToTop;
    }

    private boolean recyclerViewScrolledToBottom() {
        boolean scrollToBottom = mRecyclerView.computeVerticalScrollExtent()
                + mRecyclerView.computeVerticalScrollOffset()
                >= mRecyclerView.computeVerticalScrollRange();
        if (scrollToBottom) {
            Log.d(TAG, "recyclerViewScrolledToBottom: ");
        }
        return scrollToBottom;
    }


    private void initialize() {
        mScroller = new Scroller(getContext());
        mScroller.setFriction(SCROLL_FLING_FRICTION);
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

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
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

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
