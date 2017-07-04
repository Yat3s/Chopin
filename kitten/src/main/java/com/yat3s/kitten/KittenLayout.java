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

    // Support child view count nested in this.
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
     * If set true, it will auto trigger load more while visible
     * item < {@link #mLoadMoreRemainShowItemCount}
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

    /**
     * Obtain content view after inflated, and it can be only support nested {@link #SUPPORT_CHILD_COUNT}
     * Setup auto load more if content view is RecyclerView, {@see} {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > SUPPORT_CHILD_COUNT) {
            throw new IllegalArgumentException("It can ONLY set ONE child view!");
        } else if (getChildCount() == SUPPORT_CHILD_COUNT) {
            mContentView = getChildAt(0);

            // Set up auto load more if content view is RecyclerView.
            if (mContentView instanceof RecyclerView) {
                setupRecyclerViewAutoLoadMore((RecyclerView) mContentView);
            }
        }
        super.onFinishInflate();
    }

    /**
     * Measure children.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int idx = 0; idx < getChildCount(); idx++) {
            measureChild(getChildAt(idx), widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Layout content view in suitable position.
     * Layout refresh header indicator on top of content view and layout loading footer indicator on
     * the bottom of content view in order to hide in the default status.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout content view.
        mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());

        // Layout refresh header indicator on top of content view.
        if (null != mRefreshHeaderIndicator) {
            mRefreshHeaderIndicator.layout(0, -mRefreshHeaderIndicator.getMeasuredHeight(),
                    mRefreshHeaderIndicator.getMeasuredWidth(), 0);
        }

        // Layout loading footer indicator on the bottom of content view.
        if (null != mLoadingFooterIndicator) {
            mLoadingFooterIndicator.layout(0,
                    mContentView.getMeasuredHeight(),
                    mLoadingFooterIndicator.getMeasuredWidth(),
                    mContentView.getMeasuredHeight() + mLoadingFooterIndicator.getMeasuredHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null == mRefreshHeaderIndicator && null == mLoadingFooterIndicator) {
            return super.dispatchTouchEvent(ev);
        }
        boolean dispatch = super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // Dispatch ACTION_DOWN event to child for process if child never consume
                // this event.
                super.dispatchTouchEvent(ev);

                // FORCE to dispatch this motion for it's going to process all event while
                // child not consume this event. for example: it nested with a LinearLayout
                // and this LinearLayout never consume this event so parent will return False
                // for disable dispatch this motion.
                // REF: it is a Recursion method, so it will execute the last child dispatch method.
                return true;
        }
        Log.d(TAG, "event--> dispatchTouchEvent: " + ev.getAction() + ", " + dispatch);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null == mRefreshHeaderIndicator && null == mLoadingFooterIndicator) {
            return super.onInterceptTouchEvent(ev);
        }
        int x = (int) ev.getX(), y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - mLastTouchX;
                int offsetY = y - mLastTouchY;

                // Intercept pull down event when it is scrolling to top.
                if (null != mRefreshHeaderIndicator && offsetY > Math.abs(offsetX)) {
                    Log.d(TAG, "canBeRefresh: " + mViewScrollChecker.canBeRefresh(this, mContentView));
                    return mViewScrollChecker.canBeRefresh(this, mContentView);
                }

                // Intercept pull up event when it is scrolling to bottom.
                if (null != mLoadingFooterIndicator && -offsetY > Math.abs(offsetX)) {
                    return mViewScrollChecker.canBeLoading(this, mContentView);
                }
        }

        boolean intercept = super.onInterceptTouchEvent(ev);
        Log.d(TAG, "event--> onInterceptTouchEvent: " + ev.getAction() + ", " + intercept);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mRefreshHeaderIndicator && null == mLoadingFooterIndicator) {
            return super.onTouchEvent(event);
        }
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = mLastTouchY - y;
                // Scroll whole view while it is needed.
                scrollBy(0, (int) (offsetY * (1 - mIndicatorScrollResistance)));

                if (null != mRefreshHeaderIndicator) {
                    int progress;
                    // Scroll distance has over refresh header indicator height.
                    if (-getScrollY() > mRefreshHeaderIndicator.getMeasuredHeight()) {
                        progress = 100;
                    } else {
                        progress = 100 * -getScrollY() / mRefreshHeaderIndicator.getMeasuredHeight();
                    }
                    mRefreshHeaderIndicatorProvider.onRefreshHeaderViewScrollChange(progress);
                }

                if (null != mLoadingFooterIndicator) {
                    // Scroll whole view while it is needed.
//                    scrollBy(0, (int) (offsetY * (1 - mIndicatorScrollResistance)));

                    int progress;
                    if (getScrollY() > mLoadingFooterIndicator.getMeasuredHeight()) {
                        progress = 100;
                    } else {
                        progress = 100 * getScrollY() / mLoadingFooterIndicator.getMeasuredHeight();
                    }

                    mLoadingFooterIndicatorProvider.onFooterViewScrollChange(progress);
                }
                mLastTouchY = y;

                return true;
            case MotionEvent.ACTION_UP:
                if (null != mRefreshHeaderIndicator && getScrollY() < 0) {
                    Log.d(TAG, "onTouchEvent: " + -getScrollY() + "," + mRefreshHeaderIndicator.getMeasuredHeight());
                    if (isRefreshing) {
                        releaseViewToRefreshingStatus();
                    } else if (-getScrollY() >= mRefreshHeaderIndicator.getMeasuredHeight()) {
                        // Start refreshing while scrollY exceeded refresh header indicator height.
                        releaseViewToRefreshingStatus();
                        startRefresh();
                    } else {
                        // Cancel some move events while it not meet refresh or loading demands.
                        releaseViewToDefaultStatus();
                    }
                }

                if (null != mLoadingFooterIndicator && getScrollY() > 0) {
                    if (isLoadingMore) {
                        releaseViewToLoadingStatus();
                    } else if (getScrollY() >= mLoadingFooterIndicator.getMeasuredHeight()) {
                        releaseViewToLoadingStatus();
                        startLoading();
                    } else {
                        releaseViewToDefaultStatus();

                    }
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
        mScroller.startScroll(0, getScrollY(), 0, -(mRefreshHeaderIndicator.getMeasuredHeight() + getScrollY()),
                SCROLLER_DURATION);
    }

    private void releaseViewToLoadingStatus() {
        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - mLoadingFooterIndicator.getMeasuredHeight()),
                SCROLLER_DURATION);
    }

    private void releaseViewToDefaultStatus() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
    }

    private void startRefresh() {
        isRefreshing = true;
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshStart();
        }
    }

    private void startLoading() {
        isLoadingMore = true;
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onLoadingStart();
        }
    }

    public void refreshComplete() {
        releaseViewToDefaultStatus();
        isRefreshing = false;
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshComplete();
        }
    }

    public void loadMoreComplete() {
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
        mRefreshHeaderIndicator = refreshHeaderIndicatorProvider.getContentView();
        if (null != mRefreshHeaderIndicator) {
            mRefreshHeaderIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mRefreshHeaderIndicator);
        }
    }

    public void setLoadingFooterIndicator(LoadingFooterIndicatorProvider loadingFooterIndicatorProvider) {
        mLoadingFooterIndicatorProvider = loadingFooterIndicatorProvider;
        mLoadingFooterIndicator = loadingFooterIndicatorProvider.getContentView();
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
