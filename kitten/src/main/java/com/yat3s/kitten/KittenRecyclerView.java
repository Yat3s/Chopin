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

import com.yat3s.kitten.decoration.LoadingFooterIndicatorProvider;
import com.yat3s.kitten.decoration.RefreshHeaderIndicatorProvider;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class KittenRecyclerView extends ViewGroup {
    private static final String TAG = "NimbleRecyclerView";
    private static final int SCROLLER_DURATION = 800;
    private static final float SCROLL_RESISTANCE = 0.64f;

    private RecyclerView mRecyclerView;

    // The last touch y while intercepted touch event.
    private int mLastTouchY;

    // The Scroller for scroll whole view natural.
    private Scroller mScroller;

    // The Refresh Header View.
    private View mRefreshHeaderIndicator;

    // The Loading Footer View.
    private View mLoadingFooterIndicator;

    // The provider for provide header indicator and some interfaces for interaction, eg. header indicator animation.
    private RefreshHeaderIndicatorProvider mRefreshHeaderIndicatorProvider;

    // The provider for provide footer indicator and some interfaces for interaction, eg. footer indicator animation.
    private LoadingFooterIndicatorProvider mLoadingFooterIndicatorProvider;

    // Knowing whether recycler view is refreshing.
    private boolean isRefreshing;

    // Knowing whether recycler view is loading more.
    private boolean isLoadingMore;

    // The refresh listener
    private OnRefreshListener mOnRefreshListener;

    // The load more listener
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * Set visible threshold count while {@link #autoTriggerLoadMore} is true,
     */
    private int mLoadMoreRemainItemCount = 2;

    /**
     * If set true, it will auto trigger load more while visible item < {@link #mLoadMoreRemainItemCount}
     */
    private boolean autoTriggerLoadMore = false;

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

    public void setRefreshHeaderIndicator(RefreshHeaderIndicatorProvider refreshHeaderIndicatorProvider) {
        mRefreshHeaderIndicatorProvider = refreshHeaderIndicatorProvider;
        mRefreshHeaderIndicator = refreshHeaderIndicatorProvider.provideContentView();
        if (null != mRefreshHeaderIndicator) {
            mRefreshHeaderIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mRefreshHeaderIndicator);
        }
    }

    public void setLoadingFooterIndicator(LoadingFooterIndicatorProvider loadingFooterIndicatorProvider) {
        mLoadingFooterIndicatorProvider = loadingFooterIndicatorProvider;
        mLoadingFooterIndicator = loadingFooterIndicatorProvider.provideContentView();
        if (null != mLoadingFooterIndicator) {
            mLoadingFooterIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mLoadingFooterIndicator);
        }

        // You can only choose a load more style.
        autoTriggerLoadMore = false;
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
        // Measure recycler view.
        mRecyclerView.layout(0, 0, mRecyclerView.getMeasuredWidth(), mRecyclerView.getMeasuredHeight());

        // Measure refresh header indicator.
        if (null != mRefreshHeaderIndicator) {
            mRefreshHeaderIndicator.layout(0, -mRefreshHeaderIndicator.getMeasuredHeight(), mRefreshHeaderIndicator
                    .getMeasuredWidth(), 0);
        }

        // Measure loading footer indicator.
        if (null != mLoadingFooterIndicator) {
            mLoadingFooterIndicator.layout(0,
                    mRecyclerView.getMeasuredHeight(),
                    mLoadingFooterIndicator.getMeasuredWidth(),
                    mRecyclerView.getMeasuredHeight() + mLoadingFooterIndicator.getMeasuredHeight());
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
//                Log.d(TAG, "Scroll to : " + (y - mLastTouchY > 0 ? "Top" : "Bottom"));

                // Intercept pull down event when scroll to top.
                if (y - mLastTouchY > 0) {
                    return recyclerViewScrolledToTop();
                }

                // Intercept pull up event when scroll to bottom.
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

                if (null != mRefreshHeaderIndicatorProvider) {
                    int progress;
                    // Scroll distance has over refresh header indicator height.
                    if (-getScrollY() > mRefreshHeaderIndicator.getMeasuredHeight()) {
                        progress = 100;
                    } else {
                        progress = 100 * -getScrollY() / mRefreshHeaderIndicator.getMeasuredHeight();
                    }
                    mRefreshHeaderIndicatorProvider.onRefreshHeaderViewScrollChange(progress);
                }

                return true;
            case MotionEvent.ACTION_UP:
                // Ignore some action.
                if (isRefreshing) {
                    releaseViewToRefreshingStatus();
                }
                if (isLoadingMore) {
                    releaseViewToLoadingStatus();
                }
                if (canAbortThisScrollAction()) {
                    releaseViewToDefaultStatus();
                }

                // Start refresh while scrollY over refresh header indicator height.
                if (-getScrollY() >= mRefreshHeaderIndicator.getMeasuredHeight() && !isRefreshing) {
                    releaseViewToRefreshingStatus();
                    startRefresh();
                } else if (getScrollY() >= mLoadingFooterIndicator.getMeasuredHeight() && !isLoadingMore) {
                    releaseViewToLoadingStatus();
                    startLoading();
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean canAbortThisScrollAction() {
        return !isLoadingMore
                && !isRefreshing
                && -getScrollY() < mRefreshHeaderIndicator.getMeasuredHeight()
                && getScrollY() < mLoadingFooterIndicator.getMeasuredHeight();
    }

    private void releaseViewToRefreshingStatus() {
        Log.d(TAG, "releaseViewToRefreshingStatus: ");
        mScroller.startScroll(0, getScrollY(), 0, -(mRefreshHeaderIndicator.getMeasuredHeight() + getScrollY()),
                SCROLLER_DURATION);
    }

    private void releaseViewToLoadingStatus() {
        Log.d(TAG, "releaseViewToLoadingStatus: ");
        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - mLoadingFooterIndicator.getMeasuredHeight()),
                SCROLLER_DURATION);
    }

    private void releaseViewToDefaultStatus() {
        Log.d(TAG, "releaseViewToDefaultStatus: ");
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
    }

    private void startRefresh() {
        Log.d(TAG, "startRefresh: ");
        isRefreshing = true;
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshStart();
        }
    }

    private void startLoading() {
        Log.d(TAG, "startLoading: ");
        isLoadingMore = true;
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onLoadingStart();
        }
    }

    public void refreshComplete() {
        Log.d(TAG, "refreshComplete: ");
        releaseViewToDefaultStatus();
        isRefreshing = false;
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshComplete();
        }
    }

    public void loadMoreComplete() {
        Log.d(TAG, "loadMoreComplete: ");
        releaseViewToDefaultStatus();
        isLoadingMore = false;
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onLoadingComplete();
        }
    }

    private boolean recyclerViewScrolledToTop() {
        return mRecyclerView.computeVerticalScrollOffset() <= 0;
    }

    private boolean recyclerViewScrolledToBottom() {
        return mRecyclerView.computeVerticalScrollExtent()
                + mRecyclerView.computeVerticalScrollOffset()
                >= mRecyclerView.computeVerticalScrollRange();
    }

    private void initialize() {
        mScroller = new Scroller(getContext());
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Auto load more.
                if (autoTriggerLoadMore) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    // if lastVisibleItem >= totalItemCount - mLoadMoreRemainItemCount
                    // and pull down will auto trigger load more.
                    if (lastVisibleItemPosition >= totalItemCount - mLoadMoreRemainItemCount
                            && dy > 0
                            && !isLoadingMore) {
                        isLoadingMore = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
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

    /**
     * NOTE: You can ONLY choose one load more style from {@link #setLoadingFooterIndicator(LoadingFooterIndicatorProvider)}
     * and this.
     * Please remove Load Footer View while you set autoTriggerLoadMore is true.
     *
     * @param autoTriggerLoadMore If true will auto trigger load more while
     *                            remain to show item < {@link #mLoadMoreRemainItemCount}
     */
    public void setAutoTriggerLoadMore(boolean autoTriggerLoadMore) {
        this.autoTriggerLoadMore = autoTriggerLoadMore;
    }

    public void setLoadMoreRemainItemCount(int visibleThreshold) {
        mLoadMoreRemainItemCount = visibleThreshold;
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
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
