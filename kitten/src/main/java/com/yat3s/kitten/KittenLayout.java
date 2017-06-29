package com.yat3s.kitten;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yat3s.kitten.decoration.LoadingFooterIndicatorProvider;
import com.yat3s.kitten.decoration.RefreshHeaderIndicatorProvider;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class KittenLayout extends ViewGroup {
    private static final String TAG = "KittenLayout";

    private static final int SUPPORT_CHILD_COUNT = 1;

    // Scroller duration while release to do some action.
    private static final int SCROLLER_DURATION = 800;

    // Scroll resistance, if it equal 0f will scroll no any friction,
    // if it equal 1f will can not scroll.
    private float mIndicatorScrollResistance = 0.32f;

    // The last touch position while intercepted touch event.
    private int mLastTouchX, mLastTouchY;

    // The Scroller for scroll whole view natural.
    private Scroller mScroller;

    /**
     * It is used for check content view whether can be refresh/loading or other action.
     * The default checker is only check whether view has scrolled to top or bottom.
     * <p>
     * {@see} {@link DefaultViewScrollChecker#canBeRefresh(KittenLayout, View)},
     * {@link DefaultViewScrollChecker#canBeLoading(KittenLayout, View)}
     */
    private ViewScrollChecker mViewScrollChecker = new DefaultViewScrollChecker();

    // The Refresh Header View.
    private View mRefreshHeaderIndicator;

    // The Loading Footer View.
    private View mLoadingFooterIndicator;

    // The provider for provide header indicator and some interfaces for interaction,
    // eg. header indicator animation.
    private RefreshHeaderIndicatorProvider mRefreshHeaderIndicatorProvider;

    // The provider for provide footer indicator and some interfaces for interaction,
    // eg. footer indicator animation.
    private LoadingFooterIndicatorProvider mLoadingFooterIndicatorProvider;

    // Knowing whether recycler view is refreshing.
    private boolean isRefreshing;

    // Knowing whether recycler view is loading more.
    private boolean isLoadingMore;

    // The refresh listener
    private OnRefreshListener mOnRefreshListener;

    // The load more listener
    private OnLoadMoreListener mOnLoadMoreListener;

    // The content view of user set.
    private View mContentView;

    /**
     * Set visible threshold count while {@link #autoTriggerLoadMore} is true,
     */
    private int mLoadMoreRemainShowItemCount = 2;

    /**
     * If set true, it will auto trigger load more while visible item < {@link #mLoadMoreRemainShowItemCount}
     */
    private boolean autoTriggerLoadMore = false;

    public KittenLayout(Context context) {
        this(context, null);
    }

    public KittenLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KittenLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > SUPPORT_CHILD_COUNT) {
            throw new IllegalArgumentException("It can ONLY set one child view!");
        } else if (getChildCount() == SUPPORT_CHILD_COUNT) {
            mContentView = getChildAt(0);

            // Set up auto load more if content view is recycler view.
            if (mContentView instanceof RecyclerView) {
                setupRecyclerViewAutoLoadMore((RecyclerView) mContentView);
            }
        }
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
        mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());

        // Measure refresh header indicator.
        if (null != mRefreshHeaderIndicator) {
            mRefreshHeaderIndicator.layout(0, -mRefreshHeaderIndicator.getMeasuredHeight(), mRefreshHeaderIndicator
                    .getMeasuredWidth(), 0);
        }

        // Measure loading footer indicator.
        if (null != mLoadingFooterIndicator) {
            mLoadingFooterIndicator.layout(0,
                    mContentView.getMeasuredHeight(),
                    mLoadingFooterIndicator.getMeasuredWidth(),
                    mContentView.getMeasuredHeight() + mLoadingFooterIndicator.getMeasuredHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = super.dispatchTouchEvent(ev);
        Log.d(TAG, "event--> dispatchTouchEvent: " + ev.getAction() + ", " + dispatch);
        return dispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX(), y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - mLastTouchX;
                int offsetY = y - mLastTouchY;

                // Intercept pull down event when scroll to top.
                if (offsetY > Math.abs(offsetX)) {
                    return mViewScrollChecker.canBeRefresh(this, mContentView);
                }

                // Intercept pull up event when scroll to bottom.
                if (-offsetY > Math.abs(offsetX)) {
                    return mViewScrollChecker.canBeLoading(this, mContentView);
                }

            case MotionEvent.ACTION_UP:

                break;
        }

        boolean intercept = super.onInterceptTouchEvent(ev);
        Log.d(TAG, "event--> onInterceptTouchEvent: " + ev.getAction() + ", " + intercept);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = mLastTouchY - y;

                scrollBy(0, (int) (offsetY * (1 - mIndicatorScrollResistance)));
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

                if (null != mLoadingFooterIndicatorProvider) {
                    int progress;
                    if (getScrollY() > mLoadingFooterIndicator.getMeasuredHeight()) {
                        progress = 100;
                    } else {
                        progress = 100 * getScrollY() / mLoadingFooterIndicator.getMeasuredHeight();
                    }

                    mLoadingFooterIndicatorProvider.onFooterViewScrollChange(progress);
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

        boolean touch = super.onTouchEvent(event);
        Log.d(TAG, "event--> onTouchEvent: " + event.getAction() + ", " + touch);

        return touch;
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

    private void initialize() {
        mScroller = new Scroller(getContext());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
        }
        postInvalidate();
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

    /**
     * Setup auto trigger load more.
     *
     * @param recyclerView
     */
    private void setupRecyclerViewAutoLoadMore(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (lastVisibleItemPosition >= totalItemCount - mLoadMoreRemainShowItemCount
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

    /**
     * NOTE: It can ONLY be used for {@link android.support.v7.widget.RecyclerView} and {@link android.widget.AbsListView}
     * {@ref} {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     * <p>
     * NOTE: You can ONLY choose one load more style from {@link #setLoadingFooterIndicator(LoadingFooterIndicatorProvider)}
     * and this.
     * Please remove Load Footer View while you set autoTriggerLoadMore is true.
     *
     * @param autoTriggerLoadMore If true will auto trigger load more while
     *                            remain to show item < {@link #mLoadMoreRemainShowItemCount}
     */
    public void setAutoTriggerLoadMore(boolean autoTriggerLoadMore) {
        this.autoTriggerLoadMore = autoTriggerLoadMore;
    }

    /**
     * NOTE: It can ONLY be used for {@link android.support.v7.widget.RecyclerView} and {@link android.widget.AbsListView}
     * NOTE: It can ONLY be used when {@link #autoTriggerLoadMore} is true.
     *
     * @param remainShowItemCount
     */
    public void setLoadMoreRemainItemCount(int remainShowItemCount) {
        mLoadMoreRemainShowItemCount = remainShowItemCount;
    }

    /**
     * Set indicator scroll resistance,
     * as the resistance coefficient increases, it will become increasingly difficult to slide.
     *
     * @param indicatorScrollResistance
     */
    public void setIndicatorScrollResistance(@FloatRange(from = 0, to = 1.0f) float indicatorScrollResistance) {
        mIndicatorScrollResistance = indicatorScrollResistance;
    }

    /**
     * It is used for check content view whether can be refresh/loading or other action.
     * You can do some custom edition for do refresh/loading checking.
     *
     * @param viewScrollChecker
     */
    public void setViewScrollChecker(@NonNull ViewScrollChecker viewScrollChecker) {
        mViewScrollChecker = viewScrollChecker;
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
